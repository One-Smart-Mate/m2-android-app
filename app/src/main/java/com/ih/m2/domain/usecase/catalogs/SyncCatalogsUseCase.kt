package com.ih.m2.domain.usecase.catalogs

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.domain.usecase.card.GetCardsUseCase
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
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
    private val getCardsUseCase: GetCardsUseCase
) : SyncCatalogsUseCase {

    override suspend fun invoke(syncCards: Boolean): Boolean {
        return try {
            getCardTypesUseCase(true)
            getPreclassifiersUseCase(true)
            getPrioritiesUseCase(true)
            getCardsUseCase(syncCards)
            true
        } catch (e: Exception) {
            Log.e("test","Exception ${e.localizedMessage}")
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
            false
        }
    }
}