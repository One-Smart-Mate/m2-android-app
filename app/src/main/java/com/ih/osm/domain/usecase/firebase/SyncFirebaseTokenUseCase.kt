package com.ih.osm.domain.usecase.firebase

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.usecase.user.UpdateTokenUseCase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface SyncFirebaseTokenUseCase {
    suspend operator fun invoke(): Boolean
}

class SyncFirebaseTokenUseCaseImpl
    @Inject
    constructor(
        private val getFirebaseTokenUseCase: GetFirebaseTokenUseCase,
        private val sharedPreferences: SharedPreferences,
        private val updateTokenUseCase: UpdateTokenUseCase,
    ) : SyncFirebaseTokenUseCase {
        override suspend fun invoke(): Boolean {
            return try {
                val firebaseToken = FirebaseMessaging.getInstance().token.await()
                var storedToken =
                    firebaseToken.ifEmpty {
                        sharedPreferences.getFirebaseToken()
                    }
                if (storedToken.isEmpty()) {
                    // save to service
                    val token = getFirebaseTokenUseCase()
                    sharedPreferences.saveFirebaseToken(token)
                    storedToken = token
                }
                updateTokenUseCase(storedToken)
                true
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
    }
