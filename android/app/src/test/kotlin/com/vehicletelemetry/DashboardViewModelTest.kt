package com.vehicletelemetry

import app.cash.turbine.test
import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.domain.model.*
import com.vehicletelemetry.domain.usecase.GetLiveTelemetryUseCase
import com.vehicletelemetry.domain.usecase.ObserveWarningsUseCase
import com.vehicletelemetry.presentation.dashboard.DashboardViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var getLiveTelemetry: GetLiveTelemetryUseCase
    private lateinit var observeWarnings: ObserveWarningsUseCase
    private lateinit var vm: DashboardViewModel

    private val fakeTelemetry = TelemetryData(
        vehicleId = "VH-TEST", timestamp = Instant.now(),
        speedKmh = 80.0, rpm = 3000,
        fuelLevelPercent = 55.0, engineTempCelsius = 90.0, batteryVoltage = 13.8,
        gps = GpsCoordinate(37.7749, -122.4194),
        tirePressure = TirePressure(32.0, 32.0, 32.0, 32.0),
        doorStatus = DoorStatus(), seatbeltStatus = SeatbeltStatus(), hasWarning = false
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        getLiveTelemetry = mockk()
        every { getLiveTelemetry.invoke() }          returns flowOf(fakeTelemetry)
        every { getLiveTelemetry.connectionState() } returns flowOf(WebSocketState.Connected())
        every { getLiveTelemetry.connect() }         returns Unit
        every { getLiveTelemetry.disconnect() }      returns Unit
        every { getLiveTelemetry.reconnect() }       returns Unit
        observeWarnings = mockk()
        every { observeWarnings.invoke() } returns emptyFlow()
        vm = DashboardViewModel(getLiveTelemetry, observeWarnings)
    }

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `telemetry populates ui state`() = runTest {
        assertEquals("VH-TEST", vm.uiState.value.telemetry.vehicleId)
        assertEquals(80.0, vm.uiState.value.telemetry.speedKmh)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test fun `connected state shown`() = runTest {
        assertTrue(vm.uiState.value.connectionState.isConnected)
        assertTrue(vm.uiState.value.isLive)
    }

    @Test fun `warning appears and can be dismissed`() = runTest {
        val warning = WarningEvent("VH-TEST", "HIGH_ENGINE_TEMP",
            "Engine overheating", WarningSeverity.CRITICAL, 115.0, 110.0)
        observeWarnings = mockk()
        every { observeWarnings.invoke() } returns flowOf(warning)
        vm = DashboardViewModel(getLiveTelemetry, observeWarnings)

        assertNotNull(vm.uiState.value.activeWarning)
        assertEquals("HIGH_ENGINE_TEMP", vm.uiState.value.activeWarning?.type)

        vm.onDismissWarning()
        assertNull(vm.uiState.value.activeWarning)
    }

    @Test fun `warning added to history`() = runTest {
        val warning = WarningEvent("VH-TEST", "LOW_FUEL",
            "Fuel low", WarningSeverity.WARNING, 8.0, 10.0)
        observeWarnings = mockk()
        every { observeWarnings.invoke() } returns flowOf(warning)
        vm = DashboardViewModel(getLiveTelemetry, observeWarnings)

        assertEquals(1, vm.uiState.value.warningHistory.size)
        assertEquals("LOW_FUEL", vm.uiState.value.warningHistory[0].type)
    }

    @Test fun `reconnect calls use case`() {
        vm.onReconnect()
        verify(exactly = 1) { getLiveTelemetry.reconnect() }
    }

    @Test fun `error state reflected in ui`() = runTest {
        every { getLiveTelemetry.connectionState() } returns flowOf(WebSocketState.Error("refused"))
        vm = DashboardViewModel(getLiveTelemetry, observeWarnings)
        assertTrue(vm.uiState.value.connectionState.hasError)
        assertFalse(vm.uiState.value.isLive)
    }

    @Test fun `reconnecting state shown with attempt number`() = runTest {
        every { getLiveTelemetry.connectionState() } returns flowOf(WebSocketState.Reconnecting(3, 8000L))
        vm = DashboardViewModel(getLiveTelemetry, observeWarnings)
        val cs = vm.uiState.value.connectionState
        assertTrue(cs is WebSocketState.Reconnecting)
        assertEquals(3, (cs as WebSocketState.Reconnecting).attemptNumber)
    }

    @Test fun `fuel state computed correctly`() {
        assertEquals(FuelState.CRITICAL, fakeTelemetry.copy(fuelLevelPercent = 3.0).fuelState)
        assertEquals(FuelState.LOW,      fakeTelemetry.copy(fuelLevelPercent = 10.0).fuelState)
        assertEquals(FuelState.NORMAL,   fakeTelemetry.copy(fuelLevelPercent = 60.0).fuelState)
    }

    @Test fun `door status computed correctly`() {
        assertTrue(DoorStatus(frontLeftOpen = true).anyOpen)
        assertTrue(DoorStatus(rearRightOpen = true).anyOpen)
        assertFalse(DoorStatus().anyOpen)
    }

    @Test fun `tire pressure low detection`() {
        assertTrue(TirePressure(27.0, 32.0, 32.0, 32.0).anyLow)
        assertFalse(TirePressure(32.0, 32.0, 32.0, 32.0).anyLow)
    }

    @Test fun `uiState flow emits on telemetry update`() = runTest {
        vm.uiState.test {
            val item = awaitItem()
            assertEquals(80.0, item.telemetry.speedKmh)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
