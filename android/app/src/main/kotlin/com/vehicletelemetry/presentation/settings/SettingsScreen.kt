package com.vehicletelemetry.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vehicletelemetry.BuildConfig
import com.vehicletelemetry.presentation.theme.AutomotiveColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = AutomotiveColors.TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = AutomotiveColors.TextPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AutomotiveColors.Surface)
            )
        }, containerColor = AutomotiveColors.Background
    ) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeader("Connection")
            SettingsCard {
                InfoRow("WebSocket URL", BuildConfig.WS_URL)
                Spacer(Modifier.height(8.dp))
                InfoRow("Backend URL", BuildConfig.BASE_URL)
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AutomotiveColors.GaugeBg)
                ToggleRow("Auto Reconnect", "Exponential backoff, max 60s", state.autoReconnect, vm::setAutoReconnect)
            }

            SectionHeader("Display")
            SettingsCard {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Speed Unit", color = AutomotiveColors.TextPrimary, fontSize = 14.sp)
                        Text("Currently: ${if (state.useKmh) "km/h" else "mph"}", color = AutomotiveColors.TextSecondary, fontSize = 11.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("km/h" to true, "mph" to false).forEach { (lbl, isKmh) ->
                            FilterChip(
                                selected = state.useKmh == isKmh,
                                onClick = { vm.setSpeedUnit(isKmh) },
                                label = { Text(lbl) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AutomotiveColors.Accent,
                                    selectedLabelColor = AutomotiveColors.Background
                                )
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = AutomotiveColors.GaugeBg)
                ToggleRow("Warning Alerts", "Show banner on dashboard", state.showWarnings, vm::setShowWarnings)
            }

            SectionHeader("Warning Thresholds")
            SettingsCard {
                listOf(
                    "Max Speed"       to "${state.maxSpeedKmh.toInt()} km/h",
                    "Low Fuel"        to "${state.lowFuelPercent.toInt()}%",
                    "Max Engine Temp" to "${state.maxTempC.toInt()}°C",
                    "Min Battery"     to "${state.minBatteryV}V",
                    "Min Tire Press"  to "${state.minTirePsi} PSI"
                ).forEachIndexed { i, (k, v) ->
                    if (i > 0) Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text(k, color = AutomotiveColors.TextPrimary, fontSize = 13.sp)
                        Text(v, color = AutomotiveColors.Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            SectionHeader("About")
            SettingsCard {
                listOf(
                    "App Version"  to BuildConfig.VERSION_NAME,
                    "Build Type"   to BuildConfig.BUILD_TYPE,
                    "Min SDK"      to "API 29 (Android 10 / AAOS)",
                    "Architecture" to "MVVM + Clean Architecture",
                    "Protocol"     to "STOMP over WebSocket"
                ).forEachIndexed { i, (k, v) ->
                    if (i > 0) Spacer(Modifier.height(8.dp))
                    InfoRow(k, v)
                }
            }
        }
    }
}

@Composable private fun SectionHeader(title: String) =
    Text(title, color = AutomotiveColors.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

@Composable private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) =
    Column(Modifier.fillMaxWidth().background(AutomotiveColors.Surface, RoundedCornerShape(10.dp)).padding(16.dp), content = content)

@Composable private fun InfoRow(label: String, value: String) =
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(label, color = AutomotiveColors.TextSecondary, fontSize = 13.sp)
        Text(value, color = AutomotiveColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }

@Composable private fun ToggleRow(title: String, sub: String, checked: Boolean, onChange: (Boolean) -> Unit) =
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Column {
            Text(title, color = AutomotiveColors.TextPrimary, fontSize = 14.sp)
            Text(sub, color = AutomotiveColors.TextSecondary, fontSize = 11.sp)
        }
        Switch(checked, onChange, colors = SwitchDefaults.colors(
            checkedThumbColor = AutomotiveColors.Background,
            checkedTrackColor = AutomotiveColors.Accent
        ))
    }
