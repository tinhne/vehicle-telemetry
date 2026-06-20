package com.vehicletelemetry.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * TelemetryDTO — mirrors the backend JSON payload exactly.
 * @SerializedName decouples JSON field names from Kotlin property names.
 * If backend renames a field, only the annotation changes here.
 * All defaults prevent NPE when backend adds new fields.
 */
data class TelemetryDTO(
    @SerializedName("vehicleId")         val vehicleId: String          = "",
    @SerializedName("timestamp")         val timestamp: String?         = null,
    @SerializedName("speedKmh")          val speedKmh: Double           = 0.0,
    @SerializedName("rpm")               val rpm: Int                   = 0,
    @SerializedName("fuelLevelPercent")  val fuelLevelPercent: Double   = 0.0,
    @SerializedName("engineTempCelsius") val engineTempCelsius: Double  = 0.0,
    @SerializedName("batteryVoltage")    val batteryVoltage: Double     = 0.0,
    @SerializedName("latitude")          val latitude: Double           = 0.0,
    @SerializedName("longitude")         val longitude: Double          = 0.0,
    @SerializedName("tirePressureFL")    val tirePressureFL: Double     = 32.0,
    @SerializedName("tirePressureFR")    val tirePressureFR: Double     = 32.0,
    @SerializedName("tirePressureRL")    val tirePressureRL: Double     = 32.0,
    @SerializedName("tirePressureRR")    val tirePressureRR: Double     = 32.0,
    @SerializedName("doorFrontLeftOpen")  val doorFrontLeftOpen: Boolean  = false,
    @SerializedName("doorFrontRightOpen") val doorFrontRightOpen: Boolean = false,
    @SerializedName("doorRearLeftOpen")   val doorRearLeftOpen: Boolean   = false,
    @SerializedName("doorRearRightOpen")  val doorRearRightOpen: Boolean  = false,
    @SerializedName("seatbeltDriver")     val seatbeltDriver: Boolean     = true,
    @SerializedName("seatbeltPassenger")  val seatbeltPassenger: Boolean  = false,
    @SerializedName("hasWarning")         val hasWarning: Boolean         = false
)

/** WarningEventDTO — mirrors the backend warning broadcast payload. */
data class WarningEventDTO(
    @SerializedName("vehicleId")      val vehicleId: String      = "",
    @SerializedName("timestamp")      val timestamp: String?     = null,
    @SerializedName("warningType")    val warningType: String    = "",
    @SerializedName("message")        val message: String        = "",
    @SerializedName("severity")       val severity: String       = "INFO",
    @SerializedName("triggerValue")   val triggerValue: Double   = 0.0,
    @SerializedName("thresholdValue") val thresholdValue: Double = 0.0
)
