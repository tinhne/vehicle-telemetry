package com.vehicletelemetry.dto;
import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TelemetryStatsDTO {
    private String  vehicleId;
    private Instant periodFrom;
    private Instant periodTo;
    private double  averageSpeedKmh;
    private double  maxSpeedKmh;
    private long    warningCount;
}
