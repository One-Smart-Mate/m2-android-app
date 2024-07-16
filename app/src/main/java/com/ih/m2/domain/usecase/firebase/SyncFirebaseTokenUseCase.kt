package com.ih.m2.domain.usecase.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.preferences.SharedPreferences
import javax.inject.Inject

interface SyncFirebaseTokenUseCase {
    suspend operator fun invoke(): Boolean
}

class SyncFirebaseTokenUseCaseImpl @Inject constructor(
    private val getFirebaseTokenUseCase: GetFirebaseTokenUseCase,
    private val sharedPreferences: SharedPreferences
) : SyncFirebaseTokenUseCase {

    override suspend fun invoke(): Boolean {
        return try {
            val storedToken = sharedPreferences.getFirebaseToken()
            if (storedToken.isEmpty()) {
                //save to service
                val token = getFirebaseTokenUseCase()
                Log.e("Firebase","Token $token")
                sharedPreferences.saveFirebaseToken(token)
            }
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }
}