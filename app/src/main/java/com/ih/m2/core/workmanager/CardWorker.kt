package com.ih.m2.core.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.SyncCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@HiltWorker
class CardWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase,
    private val customCoroutineContext: CoroutineContext,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(customCoroutineContext) {
        try {
            val isConnected = NetworkConnection.isConnected()
            Log.e("test", "Working!!!  $isConnected")
            if (isConnected) {
                val localCardList = getCardsUseCase(localCards = true)
                Log.e("List", "Local Cards List ${localCardList.size}")
                syncCardsUseCase(localCardList)
                //delay(3000)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure()
        }
    }
}

