package com.ih.m2.core.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.card.SyncCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CardWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            Log.e("test","Wroking!!!")
            val localCardList = getCardsUseCase(localCards = true)
            syncCardsUseCase(localCardList)
            Log.e("test","Cards saved")
            Result.success()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            Result.failure()
        }
    }
}

