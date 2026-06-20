package com.vehicletelemetry.presentation.history
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vehicletelemetry.domain.model.WarningEvent
import com.vehicletelemetry.domain.usecase.ObserveWarningsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(val warnings: List<WarningEvent> = emptyList())

@HiltViewModel
class HistoryViewModel @Inject constructor(private val observeWarnings: ObserveWarningsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()
    init {
        viewModelScope.launch {
            observeWarnings().collect { w ->
                _uiState.update { it.copy(warnings = (listOf(w) + it.warnings).take(100)) }
            }
        }
    }
}
