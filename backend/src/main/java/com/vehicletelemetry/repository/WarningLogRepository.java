package com.vehicletelemetry.repository;
import com.vehicletelemetry.model.entity.WarningLog;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface WarningLogRepository extends JpaRepository<WarningLog, Long> {
    Page<WarningLog> findByVehicleIdOrderByOccurredAtDesc(String vehicleId, Pageable p);
    List<WarningLog> findByVehicleIdAndOccurredAtBetweenOrderByOccurredAtDesc(String v, Instant from, Instant to);
    long countByVehicleIdAndSeverityAndOccurredAtBetween(String v, String sev, Instant from, Instant to);
}
