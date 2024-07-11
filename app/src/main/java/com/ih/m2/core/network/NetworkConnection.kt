package com.ih.m2.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.ih.m2.domain.model.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object NetworkConnection {
    suspend fun isConnected() = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("www.google.com", 80), 5000)
                true
            }
        } catch (e: Exception) {

            false
        }
    }


    suspend fun networkStatus(context: Context): NetworkStatus {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)
        val status = when {
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkCapabilities.TRANSPORT_WIFI
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkCapabilities.TRANSPORT_CELLULAR
            else -> null
        }
        return if (status == NetworkCapabilities.TRANSPORT_WIFI) {
            if (isConnected()) NetworkStatus.WIFI_CONNECTED else NetworkStatus.WIFI_DISCONNECTED
        } else if (status == NetworkCapabilities.TRANSPORT_CELLULAR) {
            if (isConnected()) NetworkStatus.DATA_CONNECTED else NetworkStatus.DATA_DISCONNECTED
        } else {
            NetworkStatus.NO_INTERNET_ACCESS
        }
    }
}