package com.ih.osm.core.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ih.osm.core.preferences.SharedPreferences
import com.ih.osm.core.workmanager.CardWorker
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.card.SyncCardsUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltAndroidApp
class CoreApplication: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
}

class CustomWorkerFactory @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val syncCardsUseCase: SyncCardsUseCase,
    private val coroutineContext: CoroutineContext,
    private val sharedPreferences: SharedPreferences
    ): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return CardWorker(
            appContext,
            workerParameters,
            getCardsUseCase,
            syncCardsUseCase,
            coroutineContext,
            sharedPreferences
        )
    }
}