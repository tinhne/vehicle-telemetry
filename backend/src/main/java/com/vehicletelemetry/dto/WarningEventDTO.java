package com.vehicletelemetry.dto;
import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WarningEventDTO {
    private String  vehicleId;
    private Instant timestamp;
    private String  warningType;
    private String  message;
    private String  severity;
    private double  triggerValue;
    private double  thresholdValue;
}
