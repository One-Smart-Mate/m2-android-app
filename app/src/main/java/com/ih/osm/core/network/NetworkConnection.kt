package com.ih.osm.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.ih.osm.domain.model.NetworkStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object NetworkConnection {
    suspend fun isConnected() = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("www.google.com", 80), 2000)
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
        return  when {
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> NetworkStatus.WIFI_CONNECTED
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> NetworkStatus.DATA_CONNECTED
            else ->  NetworkStatus.NO_INTERNET_ACCESS
        }
//        return if (status == NetworkCapabilities.TRANSPORT_WIFI) {
//            if (isConnected()) NetworkStatus.WIFI_CONNECTED else NetworkStatus.WIFI_DISCONNECTED
//        } else if (status == NetworkCapabilities.TRANSPORT_CELLULAR) {
//            if (isConnected()) NetworkStatus.DATA_CONNECTED else NetworkStatus.DATA_DISCONNECTED
//        } else {
//            NetworkStatus.NO_INTERNET_ACCESS
//        }
    }

    fun initObserve(listener: NetworkConnectionStatus) {
        NetworkConnection.listener = listener
    }

    private var listener: NetworkConnectionStatus? = null

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val result = when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkStatus.WIFI_CONNECTED
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkStatus.DATA_CONNECTED
                else -> NetworkStatus.NO_INTERNET_ACCESS
            }
            listener?.onNetworkChange(result)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            listener?.onNetworkChange(NetworkStatus.NO_INTERNET_ACCESS)
        }
    }
}

interface NetworkConnectionStatus {
    fun onNetworkChange(networkStatus: NetworkStatus)
}