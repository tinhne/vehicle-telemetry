package com.vehicletelemetry.data.repository

import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.domain.model.TelemetryData
import com.vehicletelemetry.domain.model.WarningEvent
import kotlinx.coroutines.flow.Flow

/**
 * TelemetryRepository — contract between data and domain layers.
 *
 * Critical rule: ViewModel and UseCase ONLY depend on this interface.
 * They never import OkHttp, Room, or any infrastructure class.
 *
 * Benefit: To swap WebSocket for MQTT or gRPC, change only the Impl.
 * ViewModel, UseCase, and all UI code remain untouched.
 *
 * Testing: Inject FakeTelemetryRepository in unit tests — no real network needed.
 */
interface TelemetryRepository {
    /** Stream of live telemetry snapshots from WebSocket */
    fun getLiveTelemetry(): Flow<TelemetryData>

    /** Stream of warning events when thresholds are breached */
    fun getWarningEvents(): Flow<WarningEvent>

    /** Current WebSocket connection state */
    fun getConnectionState(): Flow<WebSocketState>

    fun connect()
    fun disconnect()
}
