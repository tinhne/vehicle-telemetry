package com.vehicletelemetry.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.Instant;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TelemetryDTO {
    private String  vehicleId;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",timezone="UTC")
    private Instant timestamp;
    private double  speedKmh;
    private int     rpm;
    private double  fuelLevelPercent;
    private double  engineTempCelsius;
    private double  batteryVoltage;
    private double  latitude;
    private double  longitude;
    private double  tirePressureFL;
    private double  tirePressureFR;
    private double  tirePressureRL;
    private double  tirePressureRR;
    private boolean doorFrontLeftOpen;
    private boolean doorFrontRightOpen;
    private boolean doorRearLeftOpen;
    private boolean doorRearRightOpen;
    private boolean seatbeltDriver;
    private boolean seatbeltPassenger;
    private boolean hasWarning;
}
