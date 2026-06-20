package com.vehicletelemetry.`data`.local.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.vehicletelemetry.`data`.local.db.entity.TelemetryEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TelemetryDao_Impl(
  __db: RoomDatabase,
) : TelemetryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTelemetryEntity: EntityInsertAdapter<TelemetryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTelemetryEntity = object : EntityInsertAdapter<TelemetryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `telemetry_cache` (`id`,`vehicle_id`,`timestamp`,`speed_kmh`,`rpm`,`fuel_pct`,`engine_temp`,`battery_v`,`latitude`,`longitude`,`has_warning`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TelemetryEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.vehicleId)
        statement.bindLong(3, entity.timestamp)
        statement.bindDouble(4, entity.speedKmh)
        statement.bindLong(5, entity.rpm.toLong())
        statement.bindDouble(6, entity.fuelLevelPercent)
        statement.bindDouble(7, entity.engineTempCelsius)
        statement.bindDouble(8, entity.batteryVoltage)
        statement.bindDouble(9, entity.latitude)
        statement.bindDouble(10, entity.longitude)
        val _tmp: Int = if (entity.hasWarning) 1 else 0
        statement.bindLong(11, _tmp.toLong())
      }
    }
  }

  public override suspend fun insert(entity: TelemetryEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfTelemetryEntity.insert(_connection, entity)
  }

  public override fun getRecent(vehicleId: String, limit: Int): Flow<List<TelemetryEntity>> {
    val _sql: String =
        "SELECT * FROM telemetry_cache WHERE vehicle_id = ? ORDER BY timestamp DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("telemetry_cache")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vehicleId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfVehicleId: Int = getColumnIndexOrThrow(_stmt, "vehicle_id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSpeedKmh: Int = getColumnIndexOrThrow(_stmt, "speed_kmh")
        val _columnIndexOfRpm: Int = getColumnIndexOrThrow(_stmt, "rpm")
        val _columnIndexOfFuelLevelPercent: Int = getColumnIndexOrThrow(_stmt, "fuel_pct")
        val _columnIndexOfEngineTempCelsius: Int = getColumnIndexOrThrow(_stmt, "engine_temp")
        val _columnIndexOfBatteryVoltage: Int = getColumnIndexOrThrow(_stmt, "battery_v")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfHasWarning: Int = getColumnIndexOrThrow(_stmt, "has_warning")
        val _result: MutableList<TelemetryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TelemetryEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpVehicleId: String
          _tmpVehicleId = _stmt.getText(_columnIndexOfVehicleId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSpeedKmh: Double
          _tmpSpeedKmh = _stmt.getDouble(_columnIndexOfSpeedKmh)
          val _tmpRpm: Int
          _tmpRpm = _stmt.getLong(_columnIndexOfRpm).toInt()
          val _tmpFuelLevelPercent: Double
          _tmpFuelLevelPercent = _stmt.getDouble(_columnIndexOfFuelLevelPercent)
          val _tmpEngineTempCelsius: Double
          _tmpEngineTempCelsius = _stmt.getDouble(_columnIndexOfEngineTempCelsius)
          val _tmpBatteryVoltage: Double
          _tmpBatteryVoltage = _stmt.getDouble(_columnIndexOfBatteryVoltage)
          val _tmpLatitude: Double
          _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          val _tmpLongitude: Double
          _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          val _tmpHasWarning: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfHasWarning).toInt()
          _tmpHasWarning = _tmp != 0
          _item =
              TelemetryEntity(_tmpId,_tmpVehicleId,_tmpTimestamp,_tmpSpeedKmh,_tmpRpm,_tmpFuelLevelPercent,_tmpEngineTempCelsius,_tmpBatteryVoltage,_tmpLatitude,_tmpLongitude,_tmpHasWarning)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatest(vehicleId: String): TelemetryEntity? {
    val _sql: String =
        "SELECT * FROM telemetry_cache WHERE vehicle_id = ? ORDER BY timestamp DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vehicleId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfVehicleId: Int = getColumnIndexOrThrow(_stmt, "vehicle_id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSpeedKmh: Int = getColumnIndexOrThrow(_stmt, "speed_kmh")
        val _columnIndexOfRpm: Int = getColumnIndexOrThrow(_stmt, "rpm")
        val _columnIndexOfFuelLevelPercent: Int = getColumnIndexOrThrow(_stmt, "fuel_pct")
        val _columnIndexOfEngineTempCelsius: Int = getColumnIndexOrThrow(_stmt, "engine_temp")
        val _columnIndexOfBatteryVoltage: Int = getColumnIndexOrThrow(_stmt, "battery_v")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfHasWarning: Int = getColumnIndexOrThrow(_stmt, "has_warning")
        val _result: TelemetryEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpVehicleId: String
          _tmpVehicleId = _stmt.getText(_columnIndexOfVehicleId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSpeedKmh: Double
          _tmpSpeedKmh = _stmt.getDouble(_columnIndexOfSpeedKmh)
          val _tmpRpm: Int
          _tmpRpm = _stmt.getLong(_columnIndexOfRpm).toInt()
          val _tmpFuelLevelPercent: Double
          _tmpFuelLevelPercent = _stmt.getDouble(_columnIndexOfFuelLevelPercent)
          val _tmpEngineTempCelsius: Double
          _tmpEngineTempCelsius = _stmt.getDouble(_columnIndexOfEngineTempCelsius)
          val _tmpBatteryVoltage: Double
          _tmpBatteryVoltage = _stmt.getDouble(_columnIndexOfBatteryVoltage)
          val _tmpLatitude: Double
          _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          val _tmpLongitude: Double
          _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          val _tmpHasWarning: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfHasWarning).toInt()
          _tmpHasWarning = _tmp != 0
          _result =
              TelemetryEntity(_tmpId,_tmpVehicleId,_tmpTimestamp,_tmpSpeedKmh,_tmpRpm,_tmpFuelLevelPercent,_tmpEngineTempCelsius,_tmpBatteryVoltage,_tmpLatitude,_tmpLongitude,_tmpHasWarning)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(vehicleId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM telemetry_cache WHERE vehicle_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vehicleId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun pruneOldRecords(vehicleId: String, keep: Int) {
    val _sql: String = """
        |
        |        DELETE FROM telemetry_cache
        |        WHERE id NOT IN (
        |            SELECT id FROM telemetry_cache
        |            WHERE vehicle_id = ?
        |            ORDER BY timestamp DESC LIMIT ?
        |        ) AND vehicle_id = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vehicleId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, keep.toLong())
        _argIndex = 3
        _stmt.bindText(_argIndex, vehicleId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll(vehicleId: String) {
    val _sql: String = "DELETE FROM telemetry_cache WHERE vehicle_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vehicleId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
