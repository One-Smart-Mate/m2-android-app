package com.ih.osm.core.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay


@HiltWorker
class AppWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncCardsUseCase: SyncCardsUseCase,
    private val notificationManager: NotificationManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Call the use case
           // syncCardsUseCase()
            Log.e("test","Executing")
            notificationManager.buildNotification("Started work","")
            delay(20000)
            notificationManager.buildNotification("Finished work","")
            Log.e("test","Finishing")
            WorkManagerUUID.deleteUUID()
            Result.success()
        } catch (e: Exception) {
            // Log the error and retry or fail
            Log.e("test","Error here!")
            e.printStackTrace()
            Result.failure()
        }
    }
}