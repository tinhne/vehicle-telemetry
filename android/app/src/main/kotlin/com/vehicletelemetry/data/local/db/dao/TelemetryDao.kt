package com.vehicletelemetry.data.local.db.dao

import androidx.room.*
import com.vehicletelemetry.data.local.db.entity.TelemetryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TelemetryEntity)

    /**
     * Reactive recent records.
     * Flow emits a new list every time the table changes — History screen
     * auto-updates without polling.
     */
    @Query("SELECT * FROM telemetry_cache WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(vehicleId: String, limit: Int = 500): Flow<List<TelemetryEntity>>

    /** Latest single record for offline "last known state" display */
    @Query("SELECT * FROM telemetry_cache WHERE vehicle_id = :vehicleId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(vehicleId: String): TelemetryEntity?

    /**
     * Delete oldest records beyond retention limit.
     * Keeps DB bounded without a full table scan — uses indexed ORDER BY.
     */
    @Query("""
        DELETE FROM telemetry_cache
        WHERE id NOT IN (
            SELECT id FROM telemetry_cache
            WHERE vehicle_id = :vehicleId
            ORDER BY timestamp DESC LIMIT :keep
        ) AND vehicle_id = :vehicleId
    """)
    suspend fun pruneOldRecords(vehicleId: String, keep: Int = 86_400)

    @Query("DELETE FROM telemetry_cache WHERE vehicle_id = :vehicleId")
    suspend fun clearAll(vehicleId: String)

    @Query("SELECT COUNT(*) FROM telemetry_cache WHERE vehicle_id = :vehicleId")
    suspend fun count(vehicleId: String): Int
}
