package com.vehicletelemetry.core.di

import com.vehicletelemetry.data.repository.TelemetryRepository
import com.vehicletelemetry.data.repository.TelemetryRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule — binds interface to its implementation.
 *
 * @Binds is more efficient than @Provides: Hilt generates a direct delegation
 * instead of calling a factory method.
 *
 * To use a fake repository in tests:
 *   Replace this module with a TestRepositoryModule in your test component.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTelemetryRepository(
        impl: TelemetryRepositoryImpl
    ): TelemetryRepository
}
