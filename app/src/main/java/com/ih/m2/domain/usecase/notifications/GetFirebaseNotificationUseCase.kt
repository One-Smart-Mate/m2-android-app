package com.ih.m2.domain.usecase.notifications

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.firebase.FirebaseNotificationType
import com.ih.m2.core.preferences.SharedPreferences
import javax.inject.Inject

interface GetFirebaseNotificationUseCase {
    suspend operator fun invoke(remove: Boolean = false): FirebaseNotificationType
}

data class GetFirebaseNotificationUseCaseImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : GetFirebaseNotificationUseCase {

    override suspend fun invoke(remove: Boolean): FirebaseNotificationType {
        return try {
            if (remove) {
                sharedPreferences.removeNotification()
                FirebaseNotificationType.UNKNOWN
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