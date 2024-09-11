package com.ih.osm.core.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@HiltWorker
class CardWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase,
    private val customCoroutineContext: CoroutineContext,
    private val sharedPreferences: SharedPreferences
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result = withContext(customCoroutineContext) {
        try {
            val isConnected = NetworkConnection.isConnected()
            if (isConnected) {
                val localCardList = getCardsUseCase(localCards = true)
                syncCardsUseCase(localCardList)
                sharedPreferences.saveLastSyncDate()
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

