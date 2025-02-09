package com.ih.osm.domain.usecase.catalogs

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.usecase.card.GetCardsUseCase
import com.ih.osm.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.osm.domain.usecase.employee.GetEmployeesUseCase
import com.ih.osm.domain.usecase.level.GetLevelsUseCase
import com.ih.osm.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.osm.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.osm.domain.usecase.priority.GetPrioritiesUseCase
import javax.inject.Inject

interface SyncCatalogsUseCase {
    suspend operator fun invoke(syncCards: Boolean = true): Boolean
}

class SyncCatalogsUseCaseImpl
    @Inject
    constructor(
        private val getCardTypesUseCase: GetCardTypesUseCase,
        private val getPrioritiesUseCase: GetPrioritiesUseCase,
        private val getPreclassifiersUseCase: GetPreclassifiersUseCase,
        private val getCardsUseCase: GetCardsUseCase,
        private val getLevelsUseCase: GetLevelsUseCase,
        private val getEmployeesUseCase: GetEmployeesUseCase,
        private val getFirebaseNotificationUseCase: GetFirebaseNotificationUseCase,
        private val fileHelper: FileHelper,
    ) : SyncCatalogsUseCase {
        override suspend fun invoke(syncCards: Boolean): Boolean {
            return try {
                if (NetworkConnection.isConnected().not()) return false
                getCardTypesUseCase(true)
                getPreclassifiersUseCase(true)
                getPrioritiesUseCase(true)
                getLevelsUseCase(true)
                getEmployeesUseCase(true)
                getCardsUseCase(syncCards)
                getFirebaseNotificationUseCase(remove = true, syncCatalogs = true)
                true
            } catch (e: Exception) {
                Log.e("test", "Exception ${e.localizedMessage}")
                fileHelper.logException(e)
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
    }
