package com.vehicletelemetry.data.remote.websocket

/**
 * WebSocketState — sealed class for every possible connection state.
 *
 * Why sealed class?
 * Exhaustive `when` expressions — the compiler forces handling every state.
 * Prevents "forgot to handle Reconnecting" bugs that show stale UI.
 *
 * State machine:
 *   Disconnected → Connecting → Connected
 *                      ↓ (on failure)
 *                  Reconnecting → Connecting (retry)
 *                      ↓ (max attempts)
 *                    Error
 */
sealed class WebSocketState {
    /** No connection. Initial state and state after clean disconnect. */
    data class Disconnected(val reason: String? = null) : WebSocketState()

    /** Attempting first connection. Show loading spinner. */
    object Connecting : WebSocketState()

    /** Connection live. Telemetry is streaming. */
    data class Connected(val connectedAt: Long = System.currentTimeMillis()) : WebSocketState()

    /**
     * Connection lost, retry in progress.
     * @param attemptNumber current attempt (1-based)
     * @param nextRetryInMs milliseconds until next retry
     */
    data class Reconnecting(val attemptNumber: Int, val nextRetryInMs: Long) : WebSocketState()

    /** Non-recoverable error or max retries exceeded. */
    data class Error(val message: String, val cause: Throwable? = null) : WebSocketState()

    val isConnected    get() = this is Connected
    val isConnecting   get() = this is Connecting
    val isReconnecting get() = this is Reconnecting
    val hasError       get() = this is Error
}
