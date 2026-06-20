package com.vehicletelemetry.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vehicletelemetry.data.local.db.dao.TelemetryDao
import com.vehicletelemetry.data.local.db.entity.TelemetryEntity

/**
 * TelemetryDatabase — Room database singleton.
 * exportSchema = false: don't write schema JSON files (cleaner for demo projects).
 * In production: exportSchema = true and add migration tests.
 */
@Database(
    entities      = [TelemetryEntity::class],
    version       = 1,
    exportSchema  = false
)
abstract class TelemetryDatabase : RoomDatabase() {
    abstract fun telemetryDao(): TelemetryDao
}
