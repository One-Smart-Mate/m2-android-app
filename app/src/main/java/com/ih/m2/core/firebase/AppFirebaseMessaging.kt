package com.ih.m2.core.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ih.m2.core.notifications.NotificationManager
import com.ih.m2.core.preferences.SharedPreferences
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.EMPTY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppFirebaseMessaging : FirebaseMessagingService() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("AppFirebaseMessaging", "onMessageReceived ${Gson().toJson(message)}")
        saveNotificationType(message)
        handleNotification(message)
    }


    private fun saveNotificationType(message: RemoteMessage) {
        try {
            if (this::sharedPreferences.isInitialized) {
                val notificationType = message.getType()
                Log.e("test","Notification $notificationType")
                if (notificationType.isNotEmpty()) {
                    sharedPreferences.saveNotificationType(notificationType)
                }
            }
        } catch (e: Exception) {
            Log.e("AppFirebaseMessaging", "saveNotificationType ${e}")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun handleNotification(message: RemoteMessage) {
        try {
            if (this::notificationManager.isInitialized) {
                notificationManager.buildNotification(
                    title = message.getTitle(),
                    description = message.getDescription()
                )
            }
        } catch (e: Exception) {
            Log.e("AppFirebaseMessaging", "handleNotification ${e}")
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

fun RemoteMessage.getType(): String {
    return this.data[FirebaseMessage.MESSAGE_TYPE].orEmpty()
}

fun RemoteMessage.getTitle(): String {
    return this.data[FirebaseMessage.TITLE].defaultIfNull(
        this.notification?.title.orEmpty()
    )
}

fun RemoteMessage.getDescription(): String {
    return this.data[FirebaseMessage.DESCRIPTION].defaultIfNull(
        this.notification?.body.orEmpty()
    )
}

object FirebaseMessage {
    const val MESSAGE_TYPE = "notification_type"
    const val TITLE = "notification_title"
    const val DESCRIPTION = "notification_description"
}