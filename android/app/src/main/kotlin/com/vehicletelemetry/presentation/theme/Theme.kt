package com.vehicletelemetry.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * AutomotiveColors — dark color palette following AAOS UX guidelines.
 * - Near-black background: reduces eye strain in vehicle environment
 * - Amber primary: classic automotive instrument color (think analog gauges)
 * - Cyan secondary: modern IVI accent color
 * - High contrast text: must be readable in sunlight and at night
 * - Distinct severity colors: green/orange/red for system status
 */
object AutomotiveColors {
    val Background    = Color(0xFF0D0D0D)  // Near-black
    val Surface       = Color(0xFF1C1C1E)  // Card backgrounds
    val SurfaceHigh   = Color(0xFF2C2C2E)  // Elevated surfaces
    val TextPrimary   = Color(0xFFEAEAEA)  // Off-white — easier on eyes than pure white
    val TextSecondary = Color(0xFF8E8E93)  // Muted gray labels
    val Accent        = Color(0xFFFFCC00)  // Amber — instruments, primary actions
    val AccentBlue    = Color(0xFF00B4D8)  // Cyan — secondary data
    val Normal        = Color(0xFF34C759)  // Green — all OK
    val Warning       = Color(0xFFFF9F0A)  // Orange — attention needed
    val Critical      = Color(0xFFFF3B30)  // Red — immediate action required
    val GaugeBg       = Color(0xFF2C2C2E)  // Gauge track background
    val GaugeTick     = Color(0xFF48484A)  // Gauge tick marks
}

private val DarkColorScheme = darkColorScheme(
    primary      = Color(0xFFFFCC00),
    onPrimary    = Color(0xFF000000),
    secondary    = Color(0xFF00B4D8),
    background   = Color(0xFF0D0D0D),
    surface      = Color(0xFF1C1C1E),
    onBackground = Color(0xFFEAEAEA),
    onSurface    = Color(0xFFEAEAEA),
    error        = Color(0xFFFF3B30),
)

@Composable
fun VehicleTelemetryTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
