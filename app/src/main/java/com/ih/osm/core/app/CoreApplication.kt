package com.ih.osm.core.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.core.workmanager.AppWorker
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CoreApplication :
    Application(),
    Configuration.Provider {
    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setWorkerFactory(workerFactory)
                .build()
}

class CustomWorkerFactory
    @Inject
    constructor(
        private val syncCardsUseCase: SyncCardsUseCase,
        private val notificationManager: NotificationManager,
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters,
        ): ListenableWorker = AppWorker(appContext, workerParameters, syncCardsUseCase, notificationManager)
    }
