package com.ih.osm.core.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ih.osm.R
import com.ih.osm.core.file.FileHelper
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
    private val notificationManager: NotificationManager,
    private val fileHelper: FileHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            notificationManager.buildNotification(
                title = applicationContext.getString(R.string.sync_cards_title),
                description = applicationContext.getString(R.string.sync_cards_start)
            )
            syncCardsUseCase(true)
            WorkManagerUUID.deleteUUID()
            notificationManager.buildNotification(
                title = applicationContext.getString(R.string.sync_cards_title),
                description = applicationContext.getString(R.string.sync_cards_complete)
            )
            Result.success()
        } catch (e: Exception) {
            // Log the error and retry or fail
            fileHelper.logException(e)
            Log.e("test", "Error here!")
            e.printStackTrace()
            Result.failure()
        }
    }
}