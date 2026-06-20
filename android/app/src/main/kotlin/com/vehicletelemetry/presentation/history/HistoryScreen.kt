package com.vehicletelemetry.presentation.history
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vehicletelemetry.domain.model.WarningEvent
import com.vehicletelemetry.domain.model.WarningSeverity
import com.vehicletelemetry.presentation.theme.AutomotiveColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit, vm: HistoryViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Warning History", color = AutomotiveColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = AutomotiveColors.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AutomotiveColors.Surface)
            )
        }, containerColor = AutomotiveColors.Background
    ) { pad ->
        if (state.warnings.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = AutomotiveColors.Normal, modifier = Modifier.size(52.dp))
                    Text("No warnings this session", color = AutomotiveColors.TextSecondary, fontSize = 16.sp)
                    Text("All systems operating normally", color = AutomotiveColors.TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("${state.warnings.size} warning(s) this session", color = AutomotiveColors.TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp)) }
                itemsIndexed(state.warnings) { _, w -> WarningCard(w) }
            }
        }
    }
}

@Composable
private fun WarningCard(w: WarningEvent) {
    val accent = when (w.severity) { WarningSeverity.CRITICAL -> AutomotiveColors.Critical; WarningSeverity.WARNING -> AutomotiveColors.Warning; else -> AutomotiveColors.AccentBlue }
    Row(Modifier.fillMaxWidth().background(AutomotiveColors.Surface, RoundedCornerShape(8.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.width(3.dp).height(40.dp).background(accent, RoundedCornerShape(2.dp)))
        Column(Modifier.weight(1f)) {
            Text(w.type.replace("_", " "), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(w.message, color = AutomotiveColors.TextPrimary, fontSize = 12.sp)
            Text("Value: %.1f  |  Threshold: %.1f".format(w.triggerValue, w.thresholdValue), color = AutomotiveColors.TextSecondary, fontSize = 11.sp)
        }
        Text(w.severity.name, color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
