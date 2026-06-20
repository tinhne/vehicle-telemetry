package com.vehicletelemetry.service;
import com.vehicletelemetry.dto.TelemetryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SensorSimulatorService — runs at 10Hz, generates realistic vehicle data.
 *
 * Physics model:
 * - Speed: random walk with mean-reversion toward 80 km/h
 * - RPM: piecewise gear-ratio simulation based on speed
 * - Engine temp: warms from 20°C → 90°C when driving (slow rise)
 * - Fuel: consumed proportional to RPM (~0.0002% per tick at 1000 RPM)
 * - Battery: 14V when engine running (alternator), 12.6V when stopped
 * - GPS: slow position drift simulating vehicle movement
 * - Tires: very slow pressure loss, auto-reset below 25 PSI
 *
 * Tick scheduling: fixedDelay (not fixedRate) — waits for previous tick
 * to complete before scheduling next. Prevents backlog if tick takes >100ms.
 *
 * In production: replace this with CAN Bus adapter read loop or Kafka consumer.
 */
@Slf4j @Service @RequiredArgsConstructor
public class SensorSimulatorService {
    private final BroadcastService  broadcast;
    private final WarningService    warningService;
    private final TelemetryService  telemetryService;

    @Value("${telemetry.simulator.vehicle-id}") private String vehicleId;

    // Stateful vehicle model — AtomicReference for thread-safe reads
    private final AtomicReference<Double> speed   = new AtomicReference<>(0.0);
    private final AtomicReference<Double> rpm     = new AtomicReference<>(800.0);
    private final AtomicReference<Double> fuel    = new AtomicReference<>(75.0);
    private final AtomicReference<Double> temp    = new AtomicReference<>(20.0);
    private final AtomicReference<Double> battery = new AtomicReference<>(12.6);
    private final AtomicReference<Double> lat     = new AtomicReference<>(37.7749);
    private final AtomicReference<Double> lon     = new AtomicReference<>(-122.4194);
    private final double[] tires = {32.5, 32.5, 31.8, 32.3};
    private final Random rng = new Random();
    private long tick = 0;

    @Scheduled(fixedDelayString = "${telemetry.simulator.broadcast-interval-ms}")
    public void tick() {
        tick++;
        TelemetryDTO snapshot = buildSnapshot();

        // 1) Broadcast to all connected WebSocket clients
        broadcast.broadcastTelemetry(snapshot);

        // 2) Check thresholds async — doesn't block the simulator
        warningService.evaluateAndBroadcast(snapshot);

        // 3) Persist to DB every 10th tick (1Hz) to limit write load
        //    At 10Hz persist rate: 600 rows/minute, 864,000/day — too much
        //    At 1Hz persist rate:  60 rows/minute, 86,400/day — manageable
        if (tick % 10 == 0) telemetryService.persistAsync(snapshot);

        if (log.isTraceEnabled()) {
            log.trace("tick={} spd={} rpm={} tmp={}", tick,
                snapshot.getSpeedKmh(), snapshot.getRpm(), snapshot.getEngineTempCelsius());
        }
    }

    private TelemetryDTO buildSnapshot() {
        // Speed: Gaussian noise + mean-reversion toward 80 km/h
        double s = speed.get() + rng.nextGaussian() * 3.0 + (80.0 - speed.get()) * 0.01;
        s = Math.max(0, Math.min(180, s));
        speed.set(s);

        // RPM: piecewise gear simulation
        double tRpm = s<5 ? 800 : s<30 ? 1500+s*30 : s<60 ? 2000+(s-30)*20 : s<100 ? 2600+(s-60)*15 : 3200+(s-100)*10;
        double r = smooth(rpm.get(), tRpm, 200) + rng.nextGaussian() * 50;
        r = Math.max(700, Math.min(7000, r));
        rpm.set(r);

        // Engine temp: slow warm-up to 90°C
        double t = smooth(temp.get(), s>5 ? 90.0 : 85.0, 0.1) + rng.nextGaussian() * 0.5;
        temp.set(Math.max(20, t));

        // Fuel: consume proportional to RPM
        fuel.set(Math.max(0, fuel.get() - (r / 1000.0) * 0.0002));

        // Battery: charge when engine running, drain when stopped
        double bv = smooth(battery.get(), s>1 ? 14.0 : 12.6, 0.05) + rng.nextGaussian() * 0.05;
        battery.set(Math.max(10.0, Math.min(15.5, bv)));

        // GPS drift
        lat.set(lat.get() + (s / 100000.0) * (rng.nextDouble() - 0.5));
        lon.set(lon.get() + (s / 100000.0) * (rng.nextDouble() - 0.5));

        // Tire pressure: very slow leak, auto-reset below 25 PSI
        for (int i = 0; i < 4; i++) {
            tires[i] = Math.max(20, Math.min(40, tires[i] - 0.0001 + rng.nextGaussian() * 0.01));
            if (tires[i] < 25) tires[i] = 32.0;
        }

        return TelemetryDTO.builder()
            .vehicleId(vehicleId).timestamp(Instant.now())
            .speedKmh(round(s,1)).rpm((int)Math.round(r))
            .fuelLevelPercent(round(fuel.get(),1))
            .engineTempCelsius(round(temp.get(),1))
            .batteryVoltage(round(battery.get(),2))
            .latitude(round(lat.get(),6)).longitude(round(lon.get(),6))
            .tirePressureFL(round(tires[0],1)).tirePressureFR(round(tires[1],1))
            .tirePressureRL(round(tires[2],1)).tirePressureRR(round(tires[3],1))
            .seatbeltDriver(true).seatbeltPassenger(false).hasWarning(false)
            .build();
    }

    private double smooth(double cur, double target, double step) {
        double diff = target - cur;
        return cur + Math.min(Math.abs(diff), step) * Math.signum(diff);
    }

    private double round(double v, int dp) {
        double scale = Math.pow(10, dp);
        return Math.round(v * scale) / scale;
    }
}
