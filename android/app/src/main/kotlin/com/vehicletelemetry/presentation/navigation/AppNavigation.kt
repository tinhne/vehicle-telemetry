package com.vehicletelemetry.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vehicletelemetry.presentation.dashboard.DashboardScreen
import com.vehicletelemetry.presentation.history.HistoryScreen
import com.vehicletelemetry.presentation.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object History   : Screen("history")
    object Settings  : Screen("settings")
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToHistory  = { nav.navigate(Screen.History.route) },
                onNavigateToSettings = { nav.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.History.route)  { HistoryScreen(onBack = { nav.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { nav.popBackStack() }) }
    }
}
