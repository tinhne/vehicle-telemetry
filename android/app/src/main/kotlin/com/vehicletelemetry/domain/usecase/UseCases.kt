package com.vehicletelemetry.domain.usecase

import com.vehicletelemetry.data.repository.TelemetryRepository
import javax.inject.Inject

/**
 * GetLiveTelemetryUseCase
 *
 * Why use cases instead of calling repository directly from ViewModel?
 * 1. Single Responsibility: ViewModel handles UI state; UseCase handles business logic
 * 2. Reusability: multiple ViewModels can use same UseCase without code duplication
 * 3. Testability: test business logic independently of ViewModel or network
 *
 * In this project the use case is thin (pure delegation). In production you
 * might add: unit conversion (km/h ↔ mph), rolling average smoothing,
 * GPS accuracy filtering, etc.
 */
class GetLiveTelemetryUseCase @Inject constructor(
    private val repo: TelemetryRepository
) {
    operator fun invoke() = repo.getLiveTelemetry()
    fun connectionState()  = repo.getConnectionState()
    fun connect()          = repo.connect()
    fun disconnect()       = repo.disconnect()
    fun reconnect()        { repo.disconnect(); repo.connect() }
}

/**
 * ObserveWarningsUseCase
 *
 * In production you might add deduplication:
 *   .distinctUntilChangedBy { it.type }  — suppress rapid repeated same warnings
 */
class ObserveWarningsUseCase @Inject constructor(
    private val repo: TelemetryRepository
) {
    operator fun invoke() = repo.getWarningEvents()
}
