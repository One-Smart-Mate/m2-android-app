package com.ih.osm.domain.usecase.notifications

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.firebase.FirebaseNotificationType
import com.ih.osm.core.preferences.SharedPreferences
import javax.inject.Inject

interface GetFirebaseNotificationUseCase {
    suspend operator fun invoke(
        remove: Boolean = false,
        syncCatalogs: Boolean = false,
        syncCards: Boolean = false
    ): FirebaseNotificationType
}

data class GetFirebaseNotificationUseCaseImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : GetFirebaseNotificationUseCase {

    override suspend fun invoke(
        remove: Boolean,
        syncCatalogs: Boolean,
        syncCards: Boolean
    ): FirebaseNotificationType {
        return try {
            if (remove) {
                when {
                    syncCards -> {
                        if (sharedPreferences.getNotificationType() == FirebaseNotificationType.SYNC_REMOTE_CARDS.name) {
                            sharedPreferences.removeNotification()
                            FirebaseNotificationType.UNKNOWN
                        }
                    }

                    syncCatalogs -> {
                        if (sharedPreferences.getNotificationType() == FirebaseNotificationType.SYNC_REMOTE_CATALOGS.name) {
                            sharedPreferences.removeNotification()
                            FirebaseNotificationType.UNKNOWN
                        }
                    }
                    else -> {
                        sharedPreferences.removeNotification()
                        FirebaseNotificationType.UNKNOWN
                    }
                }
            }
            val notificationType = sharedPreferences.getNotificationType()
            if (notificationType.isNotEmpty()) {
                FirebaseNotificationType.valueOf(notificationType)
            } else {
                FirebaseNotificationType.UNKNOWN
            }
        } catch (e: Exception) {
            sharedPreferences.removeNotification()
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseNotificationType.UNKNOWN
        }
    }

}