package com.vehicletelemetry.presentation.dashboard.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vehicletelemetry.data.remote.websocket.WebSocketState
import com.vehicletelemetry.domain.model.*
import com.vehicletelemetry.presentation.theme.AutomotiveColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConnectionBar(state: WebSocketState, vehicleId: String, lastUpdateMs: Long, modifier: Modifier = Modifier) {
    val (dotColor, label) = when (state) {
        is WebSocketState.Connected    -> AutomotiveColors.Normal        to "LIVE"
        is WebSocketState.Connecting   -> AutomotiveColors.Warning       to "CONNECTING"
        is WebSocketState.Reconnecting -> AutomotiveColors.Warning       to "RECONNECTING #${state.attemptNumber}"
        is WebSocketState.Error        -> AutomotiveColors.Critical      to "ERROR"
        else                           -> AutomotiveColors.TextSecondary to "OFFLINE"
    }
    Row(
        modifier = modifier.fillMaxWidth().background(AutomotiveColors.Surface)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(vehicleId, color = AutomotiveColors.TextSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(8.dp).background(dotColor, CircleShape))
            Text(label, color = dotColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        if (lastUpdateMs > 0)
            Text(SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(lastUpdateMs)),
                color = AutomotiveColors.TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun WarningBanner(warning: WarningEvent, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val isCritical = warning.severity == WarningSeverity.CRITICAL
    val inf = rememberInfiniteTransition(label = "pulse")
    val alpha by inf.animateFloat(
        initialValue = 1f, targetValue = if (isCritical) 0.35f else 1f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse), label = "alpha"
    )
    val (bg, accent) = when (warning.severity) {
        WarningSeverity.CRITICAL -> Color(0xFF3D0000) to AutomotiveColors.Critical
        WarningSeverity.WARNING  -> Color(0xFF2D1A00) to AutomotiveColors.Warning
        else                     -> Color(0xFF001A2D) to AutomotiveColors.AccentBlue
    }
    Row(
        modifier = modifier.fillMaxWidth().alpha(alpha)
            .background(bg, RoundedCornerShape(8.dp))
            .border(1.dp, accent, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Default.Warning, null, tint = accent, modifier = Modifier.size(20.dp))
        Column(Modifier.weight(1f)) {
            Text(warning.type.replace("_", " "), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text(warning.message, color = AutomotiveColors.TextPrimary, fontSize = 12.sp)
        }
        Text(warning.severity.name, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = onDismiss, modifier = Modifier.size(26.dp)) {
            Icon(Icons.Default.Close, null, tint = AutomotiveColors.TextSecondary, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun FuelBar(fuelPct: Double, modifier: Modifier = Modifier) {
    val color = when { fuelPct < 5 -> AutomotiveColors.Critical; fuelPct < 15 -> AutomotiveColors.Warning; else -> AutomotiveColors.Normal }
    StatusCard(modifier) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("FUEL", color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text("${fuelPct.toInt()}%", color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(5.dp))
        LinearProgressIndicator(progress = { (fuelPct / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = color, trackColor = AutomotiveColors.GaugeBg, strokeCap = StrokeCap.Round)
    }
}

@Composable
fun TempBar(tempC: Double, modifier: Modifier = Modifier) {
    val color = when { tempC > 110 -> AutomotiveColors.Critical; tempC > 100 -> AutomotiveColors.Warning; tempC < 60 -> AutomotiveColors.AccentBlue; else -> AutomotiveColors.Normal }
    StatusCard(modifier) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("ENGINE", color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text("${tempC.toInt()}°C", color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(5.dp))
        LinearProgressIndicator(progress = { ((tempC - 20.0) / 100.0).toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = color, trackColor = AutomotiveColors.GaugeBg, strokeCap = StrokeCap.Round)
    }
}

@Composable
fun BatteryPanel(voltage: Double, modifier: Modifier = Modifier) {
    val color = when { voltage < 11.5 -> AutomotiveColors.Critical; voltage < 12.5 -> AutomotiveColors.Warning; else -> AutomotiveColors.Normal }
    StatusCard(modifier) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("BATTERY", color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
            Text("%.1fV".format(voltage), color = color, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(if (voltage >= 13.5) "CHARGING" else "DISCHARGING", color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
    }
}

@Composable
fun TirePanel(tire: TirePressure, modifier: Modifier = Modifier) {
    StatusCard(modifier) {
        Text("TIRES  PSI", color = AutomotiveColors.TextSecondary, fontSize = 10.sp, letterSpacing = 1.sp)
        Spacer(Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) { TireCell("FL", tire.fl); TireCell("FR", tire.fr) }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) { TireCell("RL", tire.rl); TireCell("RR", tire.rr) }
        }
    }
}

@Composable
private fun TireCell(pos: String, psi: Double) {
    val color = when { psi < 28.0 -> AutomotiveColors.Critical; psi > 38.0 -> AutomotiveColors.Warning; else -> AutomotiveColors.TextPrimary }
    Column(Modifier.background(AutomotiveColors.GaugeBg, RoundedCornerShape(4.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(pos, color = AutomotiveColors.TextSecondary, fontSize = 9.sp)
        Text("%.1f".format(psi), color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StatusCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) =
    Column(modifier.background(AutomotiveColors.Surface, RoundedCornerShape(10.dp)).padding(10.dp), content = content)
