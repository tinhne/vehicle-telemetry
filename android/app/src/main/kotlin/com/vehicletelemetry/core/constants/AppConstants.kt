package com.vehicletelemetry.core.constants

/**
 * AppConstants — single source of truth for all magic numbers.
 * Never scatter hardcoded values throughout the codebase.
 * In production: these would be loaded from BuildConfig (per build variant)
 * or a remote configuration service.
 */
object AppConstants {
    // Network
    const val WS_CONNECT_TIMEOUT_MS   = 10_000L
    const val WS_READ_TIMEOUT_MS      = 0L          // 0 = no timeout (persistent WS)
    const val WS_PING_INTERVAL_MS     = 30_000L     // Keep-alive ping every 30s

    // Reconnect — exponential backoff: 2s → 4s → 8s → ... → 60s max
    const val RECONNECT_BASE_DELAY_MS = 2_000L
    const val RECONNECT_MAX_DELAY_MS  = 60_000L
    const val RECONNECT_MAX_ATTEMPTS  = 10

    // WebSocket STOMP topics
    const val TOPIC_TELEMETRY         = "/topic/telemetry"
    const val TOPIC_WARNINGS          = "/topic/warnings"

    // Local DB
    const val DATABASE_NAME           = "vehicle_telemetry.db"

    // UI behaviour
    const val WARNING_AUTO_DISMISS_MS = 6_000L      // Auto-dismiss warning banner after 6s

    // Logging tags (filterable in Logcat)
    const val LOG_TAG_WS              = "VT_WebSocket"
    const val LOG_TAG_REPO            = "VT_Repository"
    const val LOG_TAG_SERVICE         = "VT_FgService"

    /** Warning thresholds — mirrors backend application.yml */
    object Thresholds {
        const val SPEED_MAX_KMH         = 200.0
        const val RPM_MAX               = 7000
        const val FUEL_LOW_PERCENT      = 15.0
        const val FUEL_CRITICAL_PERCENT = 5.0
        const val ENGINE_TEMP_MAX_C     = 110.0
        const val BATTERY_MIN_V         = 11.5
        const val TIRE_MIN_PSI          = 28.0
        const val TIRE_MAX_PSI          = 38.0
    }
}
