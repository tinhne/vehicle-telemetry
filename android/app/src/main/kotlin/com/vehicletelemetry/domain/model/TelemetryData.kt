package com.vehicletelemetry.domain.model

import java.time.Instant

/**
 * TelemetryData — core domain model. Pure Kotlin, zero framework imports.
 *
 * Why separate from TelemetryDTO?
 * - DTO mirrors JSON wire format exactly (backend can change field names)
 * - Domain model is designed for app business logic and UI
 * - Mapping DTO → Domain happens in Repository
 * - If backend renames "speedKmh" → "vehicleSpeed", only RepositoryImpl changes
 *
 * Clean Architecture rule: Domain layer has NO dependencies on
 * Android framework, OkHttp, Room, or any other library.
 */
data class TelemetryData(
    val vehicleId: String,
    val timestamp: Instant,
    // Powertrain
    val speedKmh: Double,
    val rpm: Int,
    val fuelLevelPercent: Double,
    val engineTempCelsius: Double,
    val batteryVoltage: Double,
    // Location
    val gps: GpsCoordinate,
    // Safety
    val tirePressure: TirePressure,
    val doorStatus: DoorStatus,
    val seatbeltStatus: SeatbeltStatus,
    val hasWarning: Boolean
) {
    /** Fuel state for gauge color coding */
    val fuelState: FuelState get() = when {
        fuelLevelPercent < 5.0  -> FuelState.CRITICAL
        fuelLevelPercent < 15.0 -> FuelState.LOW
        else                    -> FuelState.NORMAL
    }

    /** True when engine is in normal operating temperature range */
    val isEngineWarm: Boolean get() = engineTempCelsius in 75.0..105.0

    companion object {
        /** Placeholder state while waiting for first real WebSocket message */
        val EMPTY = TelemetryData(
            vehicleId         = "--",
            timestamp         = Instant.EPOCH,
            speedKmh          = 0.0,
            rpm               = 0,
            fuelLevelPercent  = 0.0,
            engineTempCelsius = 20.0,
            batteryVoltage    = 12.6,
            gps               = GpsCoordinate(0.0, 0.0),
            tirePressure      = TirePressure(32.0, 32.0, 32.0, 32.0),
            doorStatus        = DoorStatus(),
            seatbeltStatus    = SeatbeltStatus(),
            hasWarning        = false
        )
    }
}

enum class FuelState { NORMAL, LOW, CRITICAL }

data class GpsCoordinate(val latitude: Double, val longitude: Double)

/**
 * TirePressure — TPMS data for all four wheels.
 * FL=Front Left, FR=Front Right, RL=Rear Left, RR=Rear Right
 */
data class TirePressure(
    val fl: Double, val fr: Double,
    val rl: Double, val rr: Double
) {
    val anyLow: Boolean get() = listOf(fl, fr, rl, rr).any { it < 28.0 }
    val anyHigh: Boolean get() = listOf(fl, fr, rl, rr).any { it > 38.0 }
}

data class DoorStatus(
    val frontLeftOpen:  Boolean = false,
    val frontRightOpen: Boolean = false,
    val rearLeftOpen:   Boolean = false,
    val rearRightOpen:  Boolean = false
) {
    val anyOpen: Boolean get() =
        frontLeftOpen || frontRightOpen || rearLeftOpen || rearRightOpen
}

data class SeatbeltStatus(
    val driverFastened:    Boolean = true,
    val passengerFastened: Boolean = false
)

/**
 * WarningEvent — represents a threshold breach detected by the backend.
 * Displayed as a banner on the dashboard.
 */
data class WarningEvent(
    val vehicleId:      String,
    val type:           String,
    val message:        String,
    val severity:       WarningSeverity,
    val triggerValue:   Double,
    val thresholdValue: Double
)

enum class WarningSeverity { INFO, WARNING, CRITICAL }
