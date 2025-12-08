package com.isletme.andontv.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.text.format.Formatter
import java.net.NetworkInterface

object NetworkUtils {

    /**
     * Cihazın yerel IP adresini alır
     * WiFi veya Ethernet üzerinden bağlantı varsa IP adresini döner
     */
    fun getLocalIpAddress(context: Context): String {
        try {
            // WiFi üzerinden IP almayı dene
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null && wifiInfo.ipAddress != 0) {
                return Formatter.formatIpAddress(wifiInfo.ipAddress)
            }

            // Network interfaces üzerinden IP almayı dene (Ethernet için)
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses

                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.address.size == 4) {
                        // IPv4 adresi bul
                        val ipAddress = address.hostAddress
                        if (ipAddress != null && ipAddress.contains(".")) {
                            return ipAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "IP Bulunamadı"
    }

    /**
     * İnternet bağlantısı olup olmadığını kontrol eder
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}
