package com.vehicletelemetry.presentation.settings

import androidx.lifecycle.ViewModel
import com.vehicletelemetry.core.constants.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SettingsUiState(
    val autoReconnect:  Boolean = true,
    val useKmh:         Boolean = true,
    val showWarnings:   Boolean = true,
    val maxSpeedKmh:    Double  = AppConstants.Thresholds.SPEED_MAX_KMH,
    val lowFuelPercent: Double  = AppConstants.Thresholds.FUEL_LOW_PERCENT,
    val maxTempC:       Double  = AppConstants.Thresholds.ENGINE_TEMP_MAX_C,
    val minBatteryV:    Double  = AppConstants.Thresholds.BATTERY_MIN_V,
    val minTirePsi:     Double  = AppConstants.Thresholds.TIRE_MIN_PSI
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun setAutoReconnect(v: Boolean) = _state.update { it.copy(autoReconnect = v) }
    fun setSpeedUnit(kmh: Boolean)   = _state.update { it.copy(useKmh = kmh) }
    fun setShowWarnings(v: Boolean)  = _state.update { it.copy(showWarnings = v) }
}
