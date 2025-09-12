package com.ih.osm.core.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.ih.osm.domain.model.Card
import com.ih.osm.ui.extensions.YYYY_MM_DD_HH_MM_SS
import com.ih.osm.ui.utils.EMPTY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class SharedPreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private var sharedPreferences: SharedPreferences? = null

        companion object {
            private const val OSM_APP_PREFERENCES = "osm_app_preferences"
            private const val NETWORK_PREFERENCES = "network_preference"
            private const val LOG_FILE_PREFERENCES = "path_log_file"
            private const val LAST_SYNC_PREFERENCES = "last_sync_date"
            private const val FIREBASE_TOKEN_PREFERENCES = "firebase_token"
            private const val NOTIFICATION_TYPE_PREFERENCES = "notification_type"
            private const val NOTIFICATION_APP_VERSION = "app_version"
            private const val DUE_DATE_PREFERENCES = "due_date"
            private const val CILT_CARD_PREFERENCES = "cilt_card"

            private const val FAST_PASSWORD_BLOCKED = "fast_password_blocked"
        }

        init {
            sharedPreferences = context.getSharedPreferences(OSM_APP_PREFERENCES, Context.MODE_PRIVATE)
        }

        fun clearPreferences() {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    remove(NETWORK_PREFERENCES)
                    remove(LAST_SYNC_PREFERENCES)
                    remove(FIREBASE_TOKEN_PREFERENCES)
                    remove(NOTIFICATION_TYPE_PREFERENCES)
                    remove(NOTIFICATION_APP_VERSION)
                    remove(CILT_CARD_PREFERENCES)
                    remove(DUE_DATE_PREFERENCES)
                }
            }
        }

        fun saveNetworkPreference(network: String) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(NETWORK_PREFERENCES, network)
                }
            }
        }

        fun getNetworkPreference(): String {
            return sharedPreferences?.getString(NETWORK_PREFERENCES, EMPTY).orEmpty()
        }

        fun saveLogFile(path: String) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(LOG_FILE_PREFERENCES, path)
                }
            }
        }

        fun getLogPath(): String {
            return sharedPreferences?.getString(LOG_FILE_PREFERENCES, EMPTY).orEmpty()
        }

        fun saveLastSyncDate() {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(
                        LAST_SYNC_PREFERENCES,
                        Calendar.getInstance().time.YYYY_MM_DD_HH_MM_SS,
                    )
                }
            }
        }

        fun getLastSyncDate(): String {
            return sharedPreferences?.getString(LAST_SYNC_PREFERENCES, EMPTY).orEmpty()
        }

        fun saveFirebaseToken(token: String) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(FIREBASE_TOKEN_PREFERENCES, token)
                }
            }
        }

        fun getFirebaseToken(): String {
            return sharedPreferences?.getString(FIREBASE_TOKEN_PREFERENCES, EMPTY).orEmpty()
        }

        fun saveNotificationType(type: String = EMPTY) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(NOTIFICATION_TYPE_PREFERENCES, type.uppercase())
                }
            }
        }

        fun getNotificationType(): String {
            return sharedPreferences?.getString(
                NOTIFICATION_TYPE_PREFERENCES,
                EMPTY,
            ).orEmpty().uppercase()
        }

        fun removeNotification(withAppVersion: Boolean = false) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    remove(NOTIFICATION_TYPE_PREFERENCES)
                    if (withAppVersion) {
                        remove(NOTIFICATION_APP_VERSION)
                    }
                }
            }
        }

        fun saveAppVersion(appVersion: String) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(NOTIFICATION_APP_VERSION, appVersion)
                }
            }
        }

        fun getAppVersion(): String {
            return sharedPreferences?.getString(NOTIFICATION_APP_VERSION, EMPTY).orEmpty()
        }

        fun saveDueDate(dueDate: String) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(DUE_DATE_PREFERENCES, dueDate)
                }
            }
        }

        fun getDueDate(): String {
            return sharedPreferences?.getString(DUE_DATE_PREFERENCES, EMPTY).orEmpty()
        }

        fun saveCiltCard(card: Card) {
            val json = Gson().toJson(card)
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putString(CILT_CARD_PREFERENCES, json)
                }
            }
        }

        fun getCiltCard(): Card? {
            val json = sharedPreferences?.getString(CILT_CARD_PREFERENCES, null) ?: return null
            return try {
                Gson().fromJson(json, Card::class.java)
            } catch (e: Exception) {
                null
            }
        }

        fun removeCiltCard() {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    remove(CILT_CARD_PREFERENCES)
                }
            }
        }

        fun saveFastPasswordBlocked(blocked: Boolean) {
            sharedPreferences?.let {
                it.edit(commit = true) {
                    putBoolean(FAST_PASSWORD_BLOCKED, blocked)
                }
            }
        }

        fun isFastPasswordBlocked(): Boolean {
            return sharedPreferences?.getBoolean(FAST_PASSWORD_BLOCKED, false) ?: false
        }
    }
