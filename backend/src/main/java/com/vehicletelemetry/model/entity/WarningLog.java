package com.vehicletelemetry.model.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name="warning_logs",
    indexes=@Index(name="idx_warn_time",columnList="vehicle_id,occurred_at DESC"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WarningLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long    id;
    @Column(name="vehicle_id",  nullable=false) private String  vehicleId;
    @Column(name="occurred_at", nullable=false) private Instant occurredAt;
    @Column(name="warning_type",nullable=false) private String  warningType;
    @Column(name="severity",    nullable=false) private String  severity;
    @Column(name="message",     nullable=false) private String  message;
    @Column(name="trigger_val")   private Double triggerValue;
    @Column(name="threshold_val") private Double thresholdValue;
}
