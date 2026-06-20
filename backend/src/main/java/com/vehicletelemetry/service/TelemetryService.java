package com.vehicletelemetry.service;
import com.vehicletelemetry.dto.*;
import com.vehicletelemetry.model.entity.TelemetryRecord;
import com.vehicletelemetry.repository.TelemetryRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class TelemetryService {
    private final TelemetryRecordRepository repo;

    @Async @Transactional
    public void persistAsync(TelemetryDTO dto) {
        try {
            repo.save(TelemetryRecord.builder()
                .vehicleId(dto.getVehicleId())
                .recordedAt(dto.getTimestamp() != null ? dto.getTimestamp() : Instant.now())
                .speedKmh(dto.getSpeedKmh()).rpm(dto.getRpm())
                .fuelLevelPercent(dto.getFuelLevelPercent())
                .engineTempCelsius(dto.getEngineTempCelsius())
                .batteryVoltage(dto.getBatteryVoltage())
                .latitude(dto.getLatitude()).longitude(dto.getLongitude())
                .tirePressureFL(dto.getTirePressureFL()).tirePressureFR(dto.getTirePressureFR())
                .tirePressureRL(dto.getTirePressureRL()).tirePressureRR(dto.getTirePressureRR())
                .hasWarning(dto.isHasWarning()).build());
        } catch (Exception e) { log.error("Persist failed: {}", e.getMessage()); }
    }

    @Transactional(readOnly=true)
    public TelemetryHistoryResponse getHistory(String vehicleId, int page, int size) {
        int safe = Math.min(size, 200);
        Page<TelemetryRecord> result = repo.findByVehicleIdOrderByRecordedAtDesc(
            vehicleId, PageRequest.of(page, safe, Sort.by(Sort.Direction.DESC, "recordedAt")));
        return TelemetryHistoryResponse.builder().vehicleId(vehicleId)
            .records(result.getContent().stream().map(this::toDto).collect(Collectors.toList()))
            .totalElements(result.getTotalElements()).totalPages(result.getTotalPages())
            .currentPage(page).pageSize(safe).build();
    }

    @Transactional(readOnly=true)
    public TelemetryDTO getLatest(String vehicleId) {
        TelemetryRecord r = repo.findTopByVehicleIdOrderByRecordedAtDesc(vehicleId);
        return r == null ? null : toDto(r);
    }

    @Transactional(readOnly=true)
    public TelemetryStatsDTO getStats(String vehicleId) {
        Instant now = Instant.now(), from = now.minus(24, ChronoUnit.HOURS);
        Double avg = repo.avgSpeed(vehicleId, from, now);
        Double max = repo.maxSpeed(vehicleId, from, now);
        long cnt = repo.countByVehicleIdAndHasWarningTrueAndRecordedAtBetween(vehicleId, from, now);
        return TelemetryStatsDTO.builder().vehicleId(vehicleId).periodFrom(from).periodTo(now)
            .averageSpeedKmh(avg != null ? avg : 0.0)
            .maxSpeedKmh(max != null ? max : 0.0)
            .warningCount(cnt).build();
    }

    @Transactional
    public void deleteOlderThan(int days) {
        repo.deleteByRecordedAtBefore(Instant.now().minus(days, ChronoUnit.DAYS));
    }

    private TelemetryDTO toDto(TelemetryRecord r) {
        return TelemetryDTO.builder().vehicleId(r.getVehicleId()).timestamp(r.getRecordedAt())
            .speedKmh(r.getSpeedKmh()).rpm(r.getRpm())
            .fuelLevelPercent(r.getFuelLevelPercent()).engineTempCelsius(r.getEngineTempCelsius())
            .batteryVoltage(r.getBatteryVoltage()).latitude(r.getLatitude()).longitude(r.getLongitude())
            .tirePressureFL(r.getTirePressureFL()).tirePressureFR(r.getTirePressureFR())
            .tirePressureRL(r.getTirePressureRL()).tirePressureRR(r.getTirePressureRR())
            .hasWarning(r.getHasWarning()).build();
    }
}
