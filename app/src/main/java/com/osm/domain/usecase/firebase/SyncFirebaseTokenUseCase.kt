package com.osm.domain.usecase.firebase

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.osm.core.preferences.SharedPreferences
import com.osm.domain.usecase.user.UpdateTokenUseCase
import javax.inject.Inject

interface SyncFirebaseTokenUseCase {
    suspend operator fun invoke(): Boolean
}

class SyncFirebaseTokenUseCaseImpl @Inject constructor(
    private val getFirebaseTokenUseCase: GetFirebaseTokenUseCase,
    private val sharedPreferences: SharedPreferences,
    private val updateTokenUseCase: UpdateTokenUseCase
) : SyncFirebaseTokenUseCase {

    override suspend fun invoke(): Boolean {
        return try {
            var storedToken = sharedPreferences.getFirebaseToken()
            if (storedToken.isEmpty()) {
                //save to service
                val token = getFirebaseTokenUseCase()
                Log.e("Firebase","Token $token")
                sharedPreferences.saveFirebaseToken(token)
                storedToken = token
            }
           updateTokenUseCase(storedToken)
            Log.e("Firebase","Token $storedToken")
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }
}