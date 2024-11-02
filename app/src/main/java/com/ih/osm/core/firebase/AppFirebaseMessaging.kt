package com.ih.osm.core.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.EMPTY
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
            val notificationType = message.getType()
            if (notificationType.isNotEmpty()) {
                sharedPreferences.saveNotificationType(notificationType)
                if (notificationType == FirebaseNotificationType.UPDATE_APP.name) {
                    sharedPreferences.saveAppVersion(message.getAppVersion())
                }
            }
        } catch (e: Exception) {
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
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sharedPreferences.saveFirebaseToken(token)
    }
}

enum class FirebaseNotificationType(val type: String) {
    SYNC_REMOTE_CATALOGS("SYNC_REMOTE_CATALOGS"),
    UNKNOWN(EMPTY),
    SYNC_REMOTE_CARDS("SYNC_REMOTE_CARDS"),
    UPDATE_APP("UPDATE_APP")
}

fun RemoteMessage.getType(): String {
    return this.data[FirebaseMessageProps.MESSAGE_TYPE].orEmpty()
}

fun RemoteMessage.getAppVersion(): String {
    return this.data[FirebaseMessageProps.APP_VERSION].orEmpty()
}

fun RemoteMessage.getTitle(): String {
    return this.data[FirebaseMessageProps.TITLE].defaultIfNull(
        this.notification?.title.orEmpty()
    )
}

fun RemoteMessage.getDescription(): String {
    return this.data[FirebaseMessageProps.DESCRIPTION].defaultIfNull(
        this.notification?.body.orEmpty()
    )
}

object FirebaseMessageProps {
    const val MESSAGE_TYPE = "notification_type"
    const val TITLE = "notification_title"
    const val DESCRIPTION = "notification_description"
    const val APP_VERSION = "notification_update_app_version"
}
