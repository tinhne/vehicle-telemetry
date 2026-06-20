package com.vehicletelemetry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * VehicleTelemetryApplication — Spring Boot entry point.
 *
 * @EnableScheduling: activates @Scheduled methods (SensorSimulatorService runs at 10Hz)
 * @EnableAsync: activates @Async methods (DB persist, warning broadcast run on thread pool)
 *
 * In production: the @Scheduled sensor simulator would be replaced by
 * a CAN Bus / OBD-II adapter reader or a Kafka consumer from vehicle gateways.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class VehicleTelemetryApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleTelemetryApplication.class, args);
    }
}
