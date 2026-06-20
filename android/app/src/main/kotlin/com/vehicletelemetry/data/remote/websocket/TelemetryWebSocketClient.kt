package com.vehicletelemetry.data.remote.websocket

import com.google.gson.Gson
import com.vehicletelemetry.BuildConfig
import com.vehicletelemetry.core.constants.AppConstants
import com.vehicletelemetry.data.remote.dto.TelemetryDTO
import com.vehicletelemetry.data.remote.dto.WarningEventDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow

/**
 * TelemetryWebSocketClient
 *
 * Manages the full lifecycle of our OkHttp WebSocket connection:
 * 1. Connect / Disconnect
 * 2. STOMP protocol framing (CONNECT → SUBSCRIBE → receive MESSAGE)
 * 3. Parse frames and emit to typed Kotlin Flows
 * 4. Exponential backoff reconnect on failure
 *
 * STOMP frame format (text over WebSocket):
 *   COMMAND\n
 *   header-name:header-value\n
 *   \n
 *   optional-body^@      (^@ = null byte = frame terminator)
 *
 * Why OkHttp instead of a STOMP library?
 * Fewer dependencies. The STOMP subset we need (CONNECT + SUBSCRIBE + MESSAGE)
 * is simple enough to implement manually. Full libraries add 2-5MB to APK size.
 */
@Singleton
class TelemetryWebSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    // ── Public Flows ──────────────────────────────────────────────────────

    private val _state = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected())
    /** Current connection state — observe in ViewModel for UI indicators */
    val connectionState: StateFlow<WebSocketState> = _state.asStateFlow()

    /**
     * Live telemetry stream.
     * replay=1: new collectors (e.g. after screen rotation) get the last
     * snapshot immediately — no blank screen waiting for the next tick.
     */
    private val _telemetry = MutableSharedFlow<TelemetryDTO>(
        replay = 1, extraBufferCapacity = 128
    )
    val telemetryFlow: SharedFlow<TelemetryDTO> = _telemetry.asSharedFlow()

    /**
     * Warning events.
     * replay=0: warnings are transient — don't replay stale alerts.
     */
    private val _warnings = MutableSharedFlow<WarningEventDTO>(
        replay = 0, extraBufferCapacity = 32
    )
    val warningFlow: SharedFlow<WarningEventDTO> = _warnings.asSharedFlow()

    // ── Private State ─────────────────────────────────────────────────────
    private var socket: WebSocket? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0

    /** Coroutine scope for emitting to flows. SupervisorJob: a failed emit
     *  doesn't cancel other operations. */
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO + CoroutineName("WS-Client")
    )

    // ── Public API ────────────────────────────────────────────────────────

    fun connect() {
        if (_state.value.isConnected || _state.value.isConnecting) {
            Timber.tag(AppConstants.LOG_TAG_WS).d("Already connected/connecting")
            return
        }
        Timber.tag(AppConstants.LOG_TAG_WS).i("Connecting → ${BuildConfig.WS_URL}")
        _state.value = WebSocketState.Connecting
        socket = okHttpClient.newWebSocket(
            Request.Builder().url(BuildConfig.WS_URL).build(),
            wsListener
        )
    }

    fun disconnect() {
        cancelReconnect()
        socket?.close(1000, "Client disconnect")
        socket = null
        _state.value = WebSocketState.Disconnected("Manual disconnect")
        Timber.tag(AppConstants.LOG_TAG_WS).i("Disconnected")
    }

    // ── OkHttp WebSocket Listener ─────────────────────────────────────────

    private val wsListener = object : WebSocketListener() {

        override fun onOpen(ws: WebSocket, response: Response) {
            reconnectAttempts = 0
            _state.value = WebSocketState.Connected()
            Timber.tag(AppConstants.LOG_TAG_WS).i("✓ Connection opened")
            // Send STOMP CONNECT then subscribe to both topics
            ws.send("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\u0000")
            ws.send("SUBSCRIBE\nid:sub-telemetry\ndestination:${AppConstants.TOPIC_TELEMETRY}\n\n\u0000")
            ws.send("SUBSCRIBE\nid:sub-warnings\ndestination:${AppConstants.TOPIC_WARNINGS}\n\n\u0000")
            Timber.tag(AppConstants.LOG_TAG_WS).d("STOMP CONNECT + SUBSCRIBE sent")
        }

        override fun onMessage(ws: WebSocket, text: String) = parseStompFrame(text)

        override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
            Timber.tag(AppConstants.LOG_TAG_WS).e(t, "Failure: ${t.message}")
            _state.value = WebSocketState.Error(t.message ?: "Connection failed", t)
            scheduleReconnect()
        }

        override fun onClosing(ws: WebSocket, code: Int, reason: String) {
            ws.close(1000, null)
        }

        override fun onClosed(ws: WebSocket, code: Int, reason: String) {
            Timber.tag(AppConstants.LOG_TAG_WS).i("Closed code=$code reason=$reason")
            if (_state.value !is WebSocketState.Disconnected) scheduleReconnect()
        }
    }

    // ── STOMP Frame Parser ────────────────────────────────────────────────

    private fun parseStompFrame(raw: String) {
        try {
            val command = raw.split("\n").firstOrNull()?.trim() ?: return
            when (command) {
                "CONNECTED" -> Timber.tag(AppConstants.LOG_TAG_WS).d("STOMP CONNECTED ack")
                "MESSAGE"   -> {
                    val dest = raw.split("\n")
                        .firstOrNull { it.startsWith("destination:") }
                        ?.removePrefix("destination:")?.trim() ?: return
                    val bodyStart = raw.indexOf("\n\n").takeIf { it >= 0 } ?: return
                    val body = raw.substring(bodyStart + 2).trimEnd('\u0000', '\n', '\r')
                    when (dest) {
                        AppConstants.TOPIC_TELEMETRY ->
                            scope.launch { _telemetry.emit(gson.fromJson(body, TelemetryDTO::class.java)) }
                        AppConstants.TOPIC_WARNINGS  ->
                            scope.launch { _warnings.emit(gson.fromJson(body, WarningEventDTO::class.java)) }
                    }
                }
                "ERROR" -> {
                    val msg = raw.split("\n").firstOrNull { it.startsWith("message:") }
                        ?.removePrefix("message:") ?: "STOMP error"
                    Timber.tag(AppConstants.LOG_TAG_WS).e("STOMP ERROR: $msg")
                    _state.value = WebSocketState.Error(msg)
                }
            }
        } catch (e: Exception) {
            Timber.tag(AppConstants.LOG_TAG_WS).e(e, "Frame parse error")
        }
    }

    // ── Exponential Backoff Reconnect ─────────────────────────────────────

    /**
     * Schedule reconnect with exponential backoff + 20% random jitter.
     * Delays: 2s → 4s → 8s → 16s → 32s → 60s (capped)
     * Jitter prevents thundering herd when many vehicles reconnect simultaneously.
     */
    private fun scheduleReconnect() {
        if (reconnectAttempts >= AppConstants.RECONNECT_MAX_ATTEMPTS) {
            _state.value = WebSocketState.Error("Max reconnect attempts reached")
            Timber.tag(AppConstants.LOG_TAG_WS).e("Giving up after ${reconnectAttempts} attempts")
            return
        }
        cancelReconnect()
        val exp   = (AppConstants.RECONNECT_BASE_DELAY_MS * 2.0.pow(reconnectAttempts)).toLong()
        val jitter = (exp * 0.2 * Math.random()).toLong()
        val delay = min(exp + jitter, AppConstants.RECONNECT_MAX_DELAY_MS)
        reconnectAttempts++
        _state.value = WebSocketState.Reconnecting(reconnectAttempts, delay)
        Timber.tag(AppConstants.LOG_TAG_WS).i("Reconnect #$reconnectAttempts in ${delay}ms")
        reconnectJob = scope.launch { delay(delay); connect() }
    }

    private fun cancelReconnect() { reconnectJob?.cancel(); reconnectJob = null }
}
