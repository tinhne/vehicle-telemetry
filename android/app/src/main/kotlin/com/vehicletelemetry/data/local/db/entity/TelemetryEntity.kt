package com.vehicletelemetry.data.local.db.entity

import androidx.room.*

/**
 * TelemetryEntity — Room entity for local telemetry cache.
 *
 * Why cache locally?
 * - History screen works offline (parking garage, tunnel)
 * - On reconnect: show last known state immediately
 * - Bounded to 86,400 rows (24h at 1 rec/sec) via pruneOldRecords()
 *
 * Index on (vehicle_id, timestamp) makes time-range queries fast.
 */
@Entity(
    tableName = "telemetry_cache",
    indices   = [Index(value = ["vehicle_id", "timestamp"])]
)
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "vehicle_id")  val vehicleId: String,
    @ColumnInfo(name = "timestamp")   val timestamp: Long,        // epoch millis
    @ColumnInfo(name = "speed_kmh")   val speedKmh: Double,
    @ColumnInfo(name = "rpm")         val rpm: Int,
    @ColumnInfo(name = "fuel_pct")    val fuelLevelPercent: Double,
    @ColumnInfo(name = "engine_temp") val engineTempCelsius: Double,
    @ColumnInfo(name = "battery_v")   val batteryVoltage: Double,
    @ColumnInfo(name = "latitude")    val latitude: Double,
    @ColumnInfo(name = "longitude")   val longitude: Double,
    @ColumnInfo(name = "has_warning") val hasWarning: Boolean
)
