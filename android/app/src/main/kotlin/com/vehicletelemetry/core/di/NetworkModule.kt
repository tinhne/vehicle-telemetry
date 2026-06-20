package com.vehicletelemetry.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vehicletelemetry.BuildConfig
import com.vehicletelemetry.core.constants.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * OkHttpClient configured for persistent WebSocket.
     *
     * Key settings:
     * - readTimeout=0: CRITICAL for WebSocket. Non-zero timeout would kill
     *   the connection if no data arrives within the window. WebSocket is
     *   persistent — use STOMP heartbeats for keepalive instead.
     * - pingInterval=30s: sends WebSocket-level pings to keep connection
     *   alive through NAT/firewall idle timeouts (common on LTE in vehicles).
     * - Logging: BASIC in debug (not BODY — would spam 10Hz telemetry to logs)
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
                    else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(AppConstants.WS_CONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(AppConstants.WS_READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .pingInterval(AppConstants.WS_PING_INTERVAL_MS, TimeUnit.MILLISECONDS)
            .addInterceptor(logging)
            .build()
    }

    /**
     * Gson with ISO-8601 date format.
     * The backend sends timestamps as "2024-03-15T10:30:45.123Z".
     * We store them as String in DTO and parse to Instant in RepositoryImpl.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()
}
