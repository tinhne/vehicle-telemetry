package com.vehicletelemetry.data.repository

import com.vehicletelemetry.data.remote.dto.TelemetryDTO
import com.vehicletelemetry.data.remote.dto.WarningEventDTO
import com.vehicletelemetry.data.remote.websocket.TelemetryWebSocketClient
import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TelemetryRepositoryImpl — maps network DTOs to domain models.
 *
 * This is the only place where OkHttp/network types touch domain types.
 * Mapping here means:
 * - Backend renames "speedKmh" → only update @SerializedName in TelemetryDTO
 * - Add new sensor field → add to DTO and mapping function here
 * - ViewModel and UI never know about the change
 */
@Singleton
class TelemetryRepositoryImpl @Inject constructor(
    private val wsClient: TelemetryWebSocketClient
) : TelemetryRepository {

    override fun getLiveTelemetry(): Flow<TelemetryData> =
        wsClient.telemetryFlow.map { it.toDomain() }

    override fun getWarningEvents(): Flow<WarningEvent> =
        wsClient.warningFlow.map { it.toDomain() }

    override fun getConnectionState(): Flow<WebSocketState> =
        wsClient.connectionState

    override fun connect()    = wsClient.connect()
    override fun disconnect() = wsClient.disconnect()
}

// ── DTO → Domain mappers (private extension functions) ───────────────────

private fun TelemetryDTO.toDomain() = TelemetryData(
    vehicleId         = vehicleId,
    timestamp         = runCatching { Instant.parse(timestamp ?: "") }
                            .getOrElse { Instant.now() },
    speedKmh          = speedKmh,
    rpm               = rpm,
    fuelLevelPercent  = fuelLevelPercent,
    engineTempCelsius = engineTempCelsius,
    batteryVoltage    = batteryVoltage,
    gps               = GpsCoordinate(latitude, longitude),
    tirePressure      = TirePressure(tirePressureFL, tirePressureFR,
                                     tirePressureRL, tirePressureRR),
    doorStatus        = DoorStatus(doorFrontLeftOpen, doorFrontRightOpen,
                                   doorRearLeftOpen, doorRearRightOpen),
    seatbeltStatus    = SeatbeltStatus(seatbeltDriver, seatbeltPassenger),
    hasWarning        = hasWarning
)

private fun WarningEventDTO.toDomain() = WarningEvent(
    vehicleId      = vehicleId,
    type           = warningType,
    message        = message,
    severity       = when (severity) {
        "CRITICAL" -> WarningSeverity.CRITICAL
        "WARNING"  -> WarningSeverity.WARNING
        else       -> WarningSeverity.INFO
    },
    triggerValue   = triggerValue,
    thresholdValue = thresholdValue
)
