package com.ih.osm.domain.usecase.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ih.osm.ui.utils.EMPTY
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface GetFirebaseTokenUseCase {
    suspend operator fun invoke(): String
}

class GetFirebaseTokenUseCaseImpl @Inject constructor(
    private val firebaseMessaging: FirebaseMessaging
): GetFirebaseTokenUseCase {


    override suspend fun invoke(): String {
        return try {
            firebaseMessaging.token.await()
        }catch (e: Exception){
            Log.e("test","Exception ${e.localizedMessage}")
            EMPTY
        }
    }
}

