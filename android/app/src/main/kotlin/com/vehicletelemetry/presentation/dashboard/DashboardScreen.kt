package com.vehicletelemetry.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.presentation.dashboard.components.*
import com.vehicletelemetry.presentation.theme.AutomotiveColors

@Composable
fun DashboardScreen(
    onNavigateToHistory:  () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    vm: DashboardViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(AutomotiveColors.Background)) {
        ConnectionBar(state.connectionState, state.telemetry.vehicleId, state.lastUpdateMs)

        AnimatedVisibility(visible = state.activeWarning != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit  = slideOutVertically { -it } + fadeOut()) {
            state.activeWarning?.let { w ->
                WarningBanner(w, vm::onDismissWarning,
                    Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }

        when (val cs = state.connectionState) {
            is WebSocketState.Connecting   -> CenteredMsg("Connecting to vehicle...", true)
            is WebSocketState.Reconnecting -> CenteredMsg("Reconnecting... attempt ${cs.attemptNumber}", true)
            is WebSocketState.Error        -> ErrorPanel(cs.message, vm::onReconnect)
            else -> {
                Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    SpeedGauge(state.telemetry.speedKmh, Modifier.weight(1.1f))
                    CenterCluster(state, Modifier.weight(0.9f))
                    RpmGauge(state.telemetry.rpm, Modifier.weight(1.1f))
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FuelBar(state.telemetry.fuelLevelPercent,  Modifier.weight(1f))
                    TempBar(state.telemetry.engineTempCelsius, Modifier.weight(1f))
                    BatteryPanel(state.telemetry.batteryVoltage, Modifier.weight(1f))
                    TirePanel(state.telemetry.tirePressure, Modifier.weight(1.4f))
                }
            }
        }
    }
}

@Composable
private fun CenterCluster(state: DashboardUiState, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("${state.telemetry.speedKmh.toInt()}", color = AutomotiveColors.Accent, fontSize = 52.sp, fontWeight = FontWeight.Black)
        Text("km/h", color = AutomotiveColors.TextSecondary, fontSize = 13.sp)
        Spacer(Modifier.height(10.dp))
        Text("%.4f".format(state.telemetry.gps.latitude),  color = AutomotiveColors.TextSecondary, fontSize = 10.sp)
        Text("%.4f".format(state.telemetry.gps.longitude), color = AutomotiveColors.TextSecondary, fontSize = 10.sp)
        if (state.telemetry.doorStatus.anyOpen) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Warning, null, tint = AutomotiveColors.Warning, modifier = Modifier.size(13.dp))
                Text("DOOR OPEN", color = AutomotiveColors.Warning, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
        if (!state.telemetry.seatbeltStatus.driverFastened) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Warning, null, tint = AutomotiveColors.Critical, modifier = Modifier.size(13.dp))
                Text("SEATBELT", color = AutomotiveColors.Critical, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun CenteredMsg(msg: String, spinner: Boolean) =
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (spinner) CircularProgressIndicator(color = AutomotiveColors.Accent, modifier = Modifier.size(44.dp))
            Text(msg, color = AutomotiveColors.TextSecondary, fontSize = 16.sp)
        }
    }

@Composable
private fun ErrorPanel(msg: String, onRetry: () -> Unit) =
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Warning, null, tint = AutomotiveColors.Critical, modifier = Modifier.size(52.dp))
            Text("Connection Error", color = AutomotiveColors.Critical, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(msg, color = AutomotiveColors.TextSecondary, fontSize = 13.sp)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AutomotiveColors.Accent)) {
                Text("Reconnect", color = AutomotiveColors.Background, fontWeight = FontWeight.Bold)
            }
        }
    }
