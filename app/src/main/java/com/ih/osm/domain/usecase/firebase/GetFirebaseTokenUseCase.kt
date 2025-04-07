package com.ih.osm.domain.usecase.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface GetFirebaseTokenUseCase {
    suspend operator fun invoke(): String
}

class GetFirebaseTokenUseCaseImpl
    @Inject
    constructor(
        private val firebaseMessaging: FirebaseMessaging,
    ) : GetFirebaseTokenUseCase {
        override suspend fun invoke(): String {
            return try {
                firebaseMessaging.token.await()
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
                EMPTY
            }
        }
    }
