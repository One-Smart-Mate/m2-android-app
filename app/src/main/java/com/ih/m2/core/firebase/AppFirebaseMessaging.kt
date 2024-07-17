package com.ih.m2.core.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.ui.utils.EMPTY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessaging : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("AppFirebaseMessaging", "onMessageReceived ${Gson().toJson(message)}")
        saveNotificationType(message.data)

    }

    private fun saveNotificationType(data: MutableMap<String, String>) {
        try {
            if (this::sharedPreferences.isInitialized) {
                val notificationType = data["notification_type"].orEmpty()
                if (notificationType.isNotEmpty()) {
                    sharedPreferences.saveNotificationType(notificationType)
                }
            }
        } catch (e: Exception) {
            Log.e("AppFirebaseMessaging", "saveNotificationType ${e}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}

enum class FirebaseNotificationType(val type: String) {
    SYNC_REMOTE_CATALOGS("SYNC_REMOTE_CATALOGS"), UNKNOWN(EMPTY),
    SYNC_REMOTE_CARDS("SYNC_REMOTE_CARDS")
}