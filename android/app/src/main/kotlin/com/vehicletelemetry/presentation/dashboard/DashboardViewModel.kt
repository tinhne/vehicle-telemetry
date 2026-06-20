package com.vehicletelemetry.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicletelemetry.core.constants.AppConstants
import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.domain.model.TelemetryData
import com.vehicletelemetry.domain.model.WarningEvent
import com.vehicletelemetry.domain.usecase.GetLiveTelemetryUseCase
import com.vehicletelemetry.domain.usecase.ObserveWarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * DashboardUiState — complete snapshot of what the Dashboard screen needs.
 *
 * This is the "UI contract". Compose screen ONLY depends on this class.
 * Immutable data class: UI updates via copy(), never mutation.
 */
data class DashboardUiState(
    val telemetry:        TelemetryData        = TelemetryData.EMPTY,
    val connectionState:  WebSocketState       = WebSocketState.Connecting,
    val activeWarning:    WarningEvent?        = null,
    val isLoading:        Boolean              = true,
    val lastUpdateMs:     Long                 = 0L,
    val warningHistory:   List<WarningEvent>   = emptyList()
) {
    /** True when connection is live AND first data has arrived */
    val isLive: Boolean get() = connectionState.isConnected && !isLoading
}

/**
 * DashboardViewModel — single source of truth for dashboard UI state.
 *
 * Responsibilities:
 * 1. Initiate WebSocket connection on creation
 * 2. Collect live telemetry, connection state, and warnings into _uiState
 * 3. Auto-dismiss warning banners after configured timeout
 * 4. Expose user actions (reconnect, dismiss warning)
 * 5. Disconnect when ViewModel is cleared (screen permanently left)
 *
 * Survives configuration changes (rotation): ViewModel lifecycle > Activity.
 * The WebSocket stays connected through rotation — no reconnect needed.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getLiveTelemetry: GetLiveTelemetryUseCase,
    private val observeWarnings:  ObserveWarningsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getLiveTelemetry.connect()
        collectTelemetry()
        collectConnectionState()
        collectWarnings()
    }

    private fun collectTelemetry() = viewModelScope.launch {
        getLiveTelemetry().collect { data ->
            _uiState.update {
                it.copy(
                    telemetry    = data,
                    isLoading    = false,
                    lastUpdateMs = System.currentTimeMillis()
                )
            }
        }
    }

    private fun collectConnectionState() = viewModelScope.launch {
        getLiveTelemetry.connectionState().collect { state ->
            _uiState.update { it.copy(connectionState = state) }
        }
    }

    private fun collectWarnings() = viewModelScope.launch {
        observeWarnings().collect { warning ->
            // Show warning banner + add to history
            _uiState.update { s ->
                s.copy(
                    activeWarning  = warning,
                    warningHistory = (listOf(warning) + s.warningHistory).take(50)
                )
            }
            // Auto-dismiss after timeout
            delay(AppConstants.WARNING_AUTO_DISMISS_MS)
            _uiState.update { current ->
                // Only clear if this is still the same warning
                if (current.activeWarning == warning) current.copy(activeWarning = null)
                else current
            }
        }
    }

    // ── User actions ──────────────────────────────────────────────────────

    fun onReconnect() = getLiveTelemetry.reconnect()

    fun onDismissWarning() = _uiState.update { it.copy(activeWarning = null) }

    override fun onCleared() {
        super.onCleared()
        // Clean disconnect when user permanently leaves the screen
        getLiveTelemetry.disconnect()
    }
}
