package com.vehicletelemetry.config;

import com.vehicletelemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

/**
 * SchedulerConfig
 *
 * Async thread pool: @Async methods (DB persist, warning broadcast) run here.
 * I/O-bound tasks can use more threads than CPU cores (threads spend time waiting for DB).
 * Rule: I/O threads = CPU cores × 2. Here: 4 cores → 8 max threads.
 *
 * Queue capacity 500: backpressure — reject async tasks if queue fills up
 * (prevents memory exhaustion during DB slowdowns).
 *
 * Data retention: daily cleanup at 2AM keeps DB size bounded.
 * At 1 Hz persist rate: 86,400 rows/day. 30 days = 2.6M rows.
 * With proper indexes this is manageable, but cleanup prevents unbounded growth.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {

    private final TelemetryService telemetryService;

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        var exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(4);
        exec.setMaxPoolSize(8);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("vt-async-");
        exec.setWaitForTasksToCompleteOnShutdown(true);
        exec.setAwaitTerminationSeconds(10);
        exec.initialize();
        return exec;
    }

    /** Daily cleanup at 2AM — delete records older than 30 days */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dataRetentionCleanup() {
        log.info("Data retention cleanup starting (keep 30 days)...");
        telemetryService.deleteOlderThan(30);
        log.info("Data retention cleanup complete.");
    }
}
