package com.vehicletelemetry.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.vehicletelemetry.presentation.navigation.AppNavigation
import com.vehicletelemetry.presentation.theme.VehicleTelemetryTheme
import com.vehicletelemetry.service.TelemetryForegroundService
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity — single Activity entry point.
 *
 * Single-activity architecture: one Activity, multiple Compose screens
 * managed by Navigation Compose. No Fragments needed.
 *
 * On creation:
 * 1. Install splash screen (must be before super.onCreate)
 * 2. Start ForegroundService to keep WebSocket alive
 * 3. Set Compose content
 *
 * Configuration change (rotation):
 * Activity is recreated but ViewModel survives. WebSocket stays connected.
 * No reconnect needed on rotation — this is a key AAOS requirement.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Must be BEFORE super.onCreate
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Edge-to-edge

        // Start ForegroundService to keep WebSocket alive when UI is backgrounded
        startForegroundService(Intent(this, TelemetryForegroundService::class.java))

        setContent {
            VehicleTelemetryTheme {
                AppNavigation()
            }
        }
    }
}
