package com.ih.osm.core.preferences

import android.content.Context
import android.content.SharedPreferences
import com.ih.osm.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class SharedPreferences @Inject constructor(
    @ApplicationContext context: Context
) {

    private var sharedPreferences: SharedPreferences? = null

    companion object {
        private const val OSM_APP_PREFERENCES = "osm_app_preferences"
        private const val NETWORK_PREFERENCES = "network_preference"
        private const val LOG_FILE_PREFERENCES = "path_log_file"
        private const val LAST_SYNC_PREFERENCES = "last_sync_date"
        private const val FIREBASE_TOKEN_PREFERENCES = "firebase_token"
        private const val NOTIFICATION_TYPE_PREFERENCES = "notification_type"
    }

    init {
        sharedPreferences = context.getSharedPreferences(OSM_APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun clearPreferences() {
        sharedPreferences?.let {
            with(it.edit()) {
                remove(NETWORK_PREFERENCES)
                remove(LAST_SYNC_PREFERENCES)
                remove(FIREBASE_TOKEN_PREFERENCES)
                remove(NOTIFICATION_TYPE_PREFERENCES)
                commit()
            }
        }
    }

    fun saveNetworkPreference(network: String) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(NETWORK_PREFERENCES, network)
                commit()
            }
        }
    }

    fun getNetworkPreference(): String {
        return sharedPreferences?.getString(NETWORK_PREFERENCES, EMPTY).orEmpty()
    }

    fun saveLogFile(path: String) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(LOG_FILE_PREFERENCES, path)
                commit()
            }
        }
    }

    fun getLogPath(): String {
        return sharedPreferences?.getString(LOG_FILE_PREFERENCES, EMPTY).orEmpty()
    }

    fun saveLastSyncDate() {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(LAST_SYNC_PREFERENCES, Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS)
                commit()
            }
        }
    }

    fun getLastSyncDate(): String {
        return sharedPreferences?.getString(LAST_SYNC_PREFERENCES, EMPTY).orEmpty()
    }

    fun saveFirebaseToken(token: String) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(FIREBASE_TOKEN_PREFERENCES, token)
                commit()
            }
        }
    }

    fun getFirebaseToken(): String {
        return sharedPreferences?.getString(FIREBASE_TOKEN_PREFERENCES, EMPTY).orEmpty()
    }

    fun saveNotificationType(type: String = EMPTY) {
        sharedPreferences?.let {
            with(it.edit()) {
                putString(NOTIFICATION_TYPE_PREFERENCES, type.uppercase())
                commit()
            }
        }
    }

    fun getNotificationType(): String {
        return sharedPreferences?.getString(NOTIFICATION_TYPE_PREFERENCES, EMPTY).orEmpty().uppercase()
    }

    fun removeNotification() {
        sharedPreferences?.let {
            with(it.edit()) {
                remove(NOTIFICATION_TYPE_PREFERENCES)
                commit()
            }
        }
    }

}