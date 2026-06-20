package com.vehicletelemetry.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.vehicletelemetry.R
import com.vehicletelemetry.data.repository.TelemetryRepository
import com.vehicletelemetry.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * TelemetryForegroundService
 *
 * WHY THIS IS CRITICAL IN AAOS:
 * The driver may switch from the dashboard to Maps/Music. Without a foreground
 * service, Android kills the app process under memory pressure after a few minutes.
 * When the driver returns to the dashboard → blank screen, no data.
 *
 * Foreground Service + persistent notification tells Android:
 * "This process is actively doing important work — don't kill it."
 *
 * START_STICKY: Android restarts the service after OOM kills with a null intent.
 * This ensures the connection recovers even from aggressive memory management.
 *
 * foregroundServiceType="connectedDevice":
 * Required on Android 14+ for services maintaining persistent network connections.
 * Describes our WebSocket connection to the vehicle backend.
 */
@AndroidEntryPoint
class TelemetryForegroundService : Service() {

    @Inject lateinit var repository: TelemetryRepository

    companion object {
        const val CHANNEL_ID      = "vt_telemetry_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP     = "com.vehicletelemetry.ACTION_STOP"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.tag("VT_FgService").i("Service created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            Timber.tag("VT_FgService").i("Stop action received")
            repository.disconnect()
            stopSelf()
            return START_NOT_STICKY
        }
        // MUST call startForeground within 5s of onStartCommand to avoid ANR/crash
        startForeground(NOTIFICATION_ID, buildNotification("Monitoring vehicle data..."))
        repository.connect()
        Timber.tag("VT_FgService").i("WebSocket connection initiated")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.disconnect()
        Timber.tag("VT_FgService").i("Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Vehicle Telemetry",
            NotificationManager.IMPORTANCE_LOW  // Silent — no sound/vibration
        ).apply {
            description = "Live vehicle telemetry monitoring"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(status: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, TelemetryForegroundService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Vehicle Telemetry")
            .setContentText(status)
            .setSmallIcon(R.drawable.ic_car)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setSilent(true)
            .addAction(0, "Stop", stopIntent)
            .build()
    }
}
