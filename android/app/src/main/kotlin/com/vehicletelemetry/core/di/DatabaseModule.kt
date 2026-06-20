package com.vehicletelemetry.core.di

import android.content.Context
import androidx.room.Room
import com.vehicletelemetry.core.constants.AppConstants
import com.vehicletelemetry.data.local.db.TelemetryDatabase
import com.vehicletelemetry.data.local.db.dao.TelemetryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Room database singleton.
     *
     * fallbackToDestructiveMigration(): drops and recreates DB on schema version changes.
     * Acceptable in dev. For production: provide explicit Migration objects.
     *
     * Production note: consider SQLCipher encryption if storing sensitive
     * GPS/driver data that must comply with GDPR or automotive data privacy regs.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): TelemetryDatabase =
        Room.databaseBuilder(ctx, TelemetryDatabase::class.java, AppConstants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDao(db: TelemetryDatabase): TelemetryDao = db.telemetryDao()
}
