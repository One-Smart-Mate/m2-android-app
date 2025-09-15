package com.ih.osm.core.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ih.osm.R
import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AppWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val syncCardsUseCase: SyncCardsUseCase,
        private val notificationManager: NotificationManager,
    ) : CoroutineWorker(appContext, workerParams) {
        override suspend fun doWork(): Result =
            try {
                notificationManager.buildNotification(
                    title = applicationContext.getString(R.string.sync_cards_title),
                    description = applicationContext.getString(R.string.sync_cards_start),
                )
                syncCardsUseCase()
                WorkManagerUUID.deleteUUID()
                notificationManager.buildNotification(
                    title = applicationContext.getString(R.string.sync_cards_title),
                    description = applicationContext.getString(R.string.sync_cards_complete),
                )
                Result.success()
            } catch (e: Exception) {
                // Log the error and retry or fail
                LoggerHelperManager.logException(e)
                e.printStackTrace()
                Result.failure()
            }
    }
