package com.ih.m2.core.network

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
}