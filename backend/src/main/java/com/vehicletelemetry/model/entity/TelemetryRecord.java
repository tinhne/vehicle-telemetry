package com.vehicletelemetry.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="telemetry_records",
    indexes=@Index(name="idx_vt_time",columnList="vehicle_id,recorded_at DESC"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TelemetryRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long    id;
    @Column(name="vehicle_id",  nullable=false) private String  vehicleId;
    @Column(name="recorded_at", nullable=false) private Instant recordedAt;
    @Column(name="speed_kmh",   nullable=false) private Double  speedKmh;
    @Column(name="rpm",         nullable=false) private Integer rpm;
    @Column(name="fuel_pct",    nullable=false) private Double  fuelLevelPercent;
    @Column(name="engine_temp", nullable=false) private Double  engineTempCelsius;
    @Column(name="battery_v",   nullable=false) private Double  batteryVoltage;
    private Double latitude;
    private Double longitude;
    @Column(name="tire_fl") private Double tirePressureFL;
    @Column(name="tire_fr") private Double tirePressureFR;
    @Column(name="tire_rl") private Double tirePressureRL;
    @Column(name="tire_rr") private Double tirePressureRR;
    @Column(name="has_warning", nullable=false) private Boolean hasWarning;
}
