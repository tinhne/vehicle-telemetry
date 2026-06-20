package com.vehicletelemetry.`data`.local.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.vehicletelemetry.`data`.local.db.dao.TelemetryDao
import com.vehicletelemetry.`data`.local.db.dao.TelemetryDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TelemetryDatabase_Impl : TelemetryDatabase() {
  private val _telemetryDao: Lazy<TelemetryDao> = lazy {
    TelemetryDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "67c172c680cb27ecdc1c6efbec972572", "a68d2baaae354b4c758d0e84f5039086") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `telemetry_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `vehicle_id` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `speed_kmh` REAL NOT NULL, `rpm` INTEGER NOT NULL, `fuel_pct` REAL NOT NULL, `engine_temp` REAL NOT NULL, `battery_v` REAL NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `has_warning` INTEGER NOT NULL)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_telemetry_cache_vehicle_id_timestamp` ON `telemetry_cache` (`vehicle_id`, `timestamp`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '67c172c680cb27ecdc1c6efbec972572')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `telemetry_cache`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsTelemetryCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTelemetryCache.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("vehicle_id", TableInfo.Column("vehicle_id", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("speed_kmh", TableInfo.Column("speed_kmh", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("rpm", TableInfo.Column("rpm", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("fuel_pct", TableInfo.Column("fuel_pct", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("engine_temp", TableInfo.Column("engine_temp", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("battery_v", TableInfo.Column("battery_v", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("latitude", TableInfo.Column("latitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("longitude", TableInfo.Column("longitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsTelemetryCache.put("has_warning", TableInfo.Column("has_warning", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTelemetryCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTelemetryCache: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTelemetryCache.add(TableInfo.Index("index_telemetry_cache_vehicle_id_timestamp",
            false, listOf("vehicle_id", "timestamp"), listOf("ASC", "ASC")))
        val _infoTelemetryCache: TableInfo = TableInfo("telemetry_cache", _columnsTelemetryCache,
            _foreignKeysTelemetryCache, _indicesTelemetryCache)
        val _existingTelemetryCache: TableInfo = read(connection, "telemetry_cache")
        if (!_infoTelemetryCache.equals(_existingTelemetryCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |telemetry_cache(com.vehicletelemetry.data.local.db.entity.TelemetryEntity).
              | Expected:
              |""".trimMargin() + _infoTelemetryCache + """
              |
              | Found:
              |""".trimMargin() + _existingTelemetryCache)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "telemetry_cache")
  }

  public override fun clearAllTables() {
    super.performClear(false, "telemetry_cache")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(TelemetryDao::class, TelemetryDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun telemetryDao(): TelemetryDao = _telemetryDao.value
}
