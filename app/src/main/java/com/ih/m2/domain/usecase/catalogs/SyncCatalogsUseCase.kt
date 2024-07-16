package com.ih.m2.domain.usecase.catalogs

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.core.firebase.FirebaseNotificationType
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.employee.GetEmployeesUseCase
import com.ih.m2.domain.usecase.level.GetLevelsUseCase
import com.ih.m2.domain.usecase.notifications.GetFirebaseNotificationUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import javax.inject.Inject

interface SyncCatalogsUseCase {
    suspend operator fun invoke(syncCards: Boolean = true): Boolean
}

class SyncCatalogsUseCaseImpl @Inject constructor(
    private val getCardTypesUseCase: GetCardTypesUseCase,
    private val getPrioritiesUseCase: GetPrioritiesUseCase,
    private val getPreclassifiersUseCase: GetPreclassifiersUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val getLevelsUseCase: GetLevelsUseCase,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getFirebaseNotificationUseCase: GetFirebaseNotificationUseCase
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
            when (getFirebaseNotificationUseCase()) {
                FirebaseNotificationType.SYNC_REMOTE_CATALOGS -> {
                    getFirebaseNotificationUseCase(remove = true)
                }

                else -> {}
            }
            true
        } catch (e: Exception) {
            Log.e("test", "Exception ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}