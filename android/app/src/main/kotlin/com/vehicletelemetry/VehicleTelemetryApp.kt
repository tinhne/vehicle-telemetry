package com.vehicletelemetry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application class — entry point for Hilt DI graph construction.
 * Must be declared in AndroidManifest: android:name=".VehicleTelemetryApp"
 */
@HiltAndroidApp
class VehicleTelemetryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Debug: full logging with line numbers
        // Production: replace with remote crash reporter (Crashlytics, etc.)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
