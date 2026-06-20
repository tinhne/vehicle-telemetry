package com.vehicletelemetry.repository;
import com.vehicletelemetry.model.entity.TelemetryRecord;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;

@Repository
public interface TelemetryRecordRepository extends JpaRepository<TelemetryRecord, Long> {
    Page<TelemetryRecord> findByVehicleIdOrderByRecordedAtDesc(String vehicleId, Pageable p);
    TelemetryRecord findTopByVehicleIdOrderByRecordedAtDesc(String vehicleId);

    @Query("SELECT AVG(t.speedKmh) FROM TelemetryRecord t WHERE t.vehicleId=:id AND t.recordedAt BETWEEN :from AND :to")
    Double avgSpeed(@Param("id") String id, @Param("from") Instant from, @Param("to") Instant to);

    @Query("SELECT MAX(t.speedKmh) FROM TelemetryRecord t WHERE t.vehicleId=:id AND t.recordedAt BETWEEN :from AND :to")
    Double maxSpeed(@Param("id") String id, @Param("from") Instant from, @Param("to") Instant to);

    long countByVehicleIdAndHasWarningTrueAndRecordedAtBetween(String v, Instant from, Instant to);
    void deleteByRecordedAtBefore(Instant cutoff);
}
