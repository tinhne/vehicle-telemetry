package com.vehicletelemetry.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NetworkUtils — checks network availability before attempting WebSocket.
 *
 * Android Automotive context:
 * A vehicle may have multiple network interfaces:
 * - Built-in LTE modem (primary for backend connectivity)
 * - WiFi tethering from paired phone
 * - Ethernet (test benches / commercial vehicles)
 *
 * ConnectivityManager abstracts this — we just ask "can we reach internet?".
 * NET_CAPABILITY_VALIDATED = Android confirmed the network can reach internet
 * (not just that WiFi is connected to a router with no WAN).
 */
@Singleton
class NetworkUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val isAvailable: Boolean get() {
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    val networkType: NetworkType get() {
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return NetworkType.NONE)
            ?: return NetworkType.NONE
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.OTHER
        }
    }
}

enum class NetworkType { NONE, WIFI, CELLULAR, ETHERNET, OTHER }
