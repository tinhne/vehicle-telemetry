package com.vehicletelemetry.service;
import com.vehicletelemetry.dto.TelemetryDTO;
import com.vehicletelemetry.dto.WarningEventDTO;
import com.vehicletelemetry.model.entity.WarningLog;
import com.vehicletelemetry.repository.WarningLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WarningService — evaluates sensor values against configurable thresholds.
 *
 * Debounce logic: same warning type won't re-fire for COOLDOWN_TICKS ticks.
 * At 10Hz, COOLDOWN_TICKS=100 = 10 second cooldown between repeated warnings.
 * This prevents flooding the Android client with thousands of "LOW_FUEL" events.
 *
 * @Async: runs on the taskExecutor thread pool so the simulator tick thread
 * never blocks on threshold evaluation or DB persistence.
 */
@Slf4j @Service @RequiredArgsConstructor
public class WarningService {
    private final BroadcastService     broadcast;
    private final WarningLogRepository warningLogRepo;

    @Value("${telemetry.thresholds.speed-max-kmh}")           private double speedMax;
    @Value("${telemetry.thresholds.rpm-max}")                 private int    rpmMax;
    @Value("${telemetry.thresholds.fuel-min-percent}")        private double fuelMin;
    @Value("${telemetry.thresholds.engine-temp-max-celsius}") private double tempMax;
    @Value("${telemetry.thresholds.battery-voltage-min}")     private double battMin;
    @Value("${telemetry.thresholds.tire-pressure-min-psi}")   private double tireMin;
    @Value("${telemetry.thresholds.tire-pressure-max-psi}")   private double tireMax;

    private static final int COOLDOWN_TICKS = 100;
    private final Map<String, Long> lastFired = new ConcurrentHashMap<>();
    private long tick = 0;

    @Async
    public void evaluateAndBroadcast(TelemetryDTO t) {
        tick++;
        var warnings = new ArrayList<WarningEventDTO>();

        if (t.getSpeedKmh()          > speedMax) warnings.add(warn(t, "OVERSPEED",       "WARNING",  "Speed %.1f km/h exceeds limit".formatted(t.getSpeedKmh()), t.getSpeedKmh(), speedMax));
        if (t.getRpm()               > rpmMax)   warnings.add(warn(t, "HIGH_RPM",         "WARNING",  "RPM %d exceeds redline".formatted(t.getRpm()), t.getRpm(), rpmMax));
        if (t.getFuelLevelPercent()  < fuelMin)  warnings.add(warn(t, "LOW_FUEL",         t.getFuelLevelPercent()<5?"CRITICAL":"WARNING", "Fuel %.1f%% is low".formatted(t.getFuelLevelPercent()), t.getFuelLevelPercent(), fuelMin));
        if (t.getEngineTempCelsius() > tempMax)  warnings.add(warn(t, "HIGH_ENGINE_TEMP", "CRITICAL", "Engine %.1f°C overheating!".formatted(t.getEngineTempCelsius()), t.getEngineTempCelsius(), tempMax));
        if (t.getBatteryVoltage()    < battMin)  warnings.add(warn(t, "LOW_BATTERY",      "CRITICAL", "Battery %.2fV critical".formatted(t.getBatteryVoltage()), t.getBatteryVoltage(), battMin));

        checkTire(t, "FL", t.getTirePressureFL(), warnings);
        checkTire(t, "FR", t.getTirePressureFR(), warnings);
        checkTire(t, "RL", t.getTirePressureRL(), warnings);
        checkTire(t, "RR", t.getTirePressureRR(), warnings);

        for (var w : warnings) {
            Long last = lastFired.get(w.getWarningType());
            if (last != null && (tick - last) < COOLDOWN_TICKS) continue;
            lastFired.put(w.getWarningType(), tick);
            broadcast.broadcastWarning(w);
            persistWarning(w);
        }
    }

    private void checkTire(TelemetryDTO t, String pos, double psi, List<WarningEventDTO> list) {
        if (psi < tireMin)      list.add(warn(t, "LOW_TIRE_"+pos,  "WARNING", "Tire %s %.1f PSI low".formatted(pos,psi),  psi, tireMin));
        else if (psi > tireMax) list.add(warn(t, "HIGH_TIRE_"+pos, "WARNING", "Tire %s %.1f PSI high".formatted(pos,psi), psi, tireMax));
    }

    private void persistWarning(WarningEventDTO w) {
        try {
            warningLogRepo.save(WarningLog.builder()
                .vehicleId(w.getVehicleId()).occurredAt(Instant.now())
                .warningType(w.getWarningType()).severity(w.getSeverity())
                .message(w.getMessage()).triggerValue(w.getTriggerValue())
                .thresholdValue(w.getThresholdValue()).build());
        } catch (Exception e) { log.error("Failed to persist warning: {}", e.getMessage()); }
    }

    private WarningEventDTO warn(TelemetryDTO t, String type, String sev, String msg, double val, double threshold) {
        return WarningEventDTO.builder().vehicleId(t.getVehicleId()).timestamp(Instant.now())
            .warningType(type).severity(sev).message(msg).triggerValue(val).thresholdValue(threshold).build();
    }
}
