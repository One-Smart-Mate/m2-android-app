package com.ih.m2.domain.usecase.catalogs

import android.util.Log
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import com.ih.m2.domain.usecase.preclassifier.GetPreclassifiersUseCase
import com.ih.m2.domain.usecase.priority.GetPrioritiesUseCase
import javax.inject.Inject

interface SyncCatalogsUseCase {
    suspend operator fun invoke()
}

class SyncCatalogsUseCaseImpl @Inject constructor(
    private val getCardTypesUseCase: GetCardTypesUseCase,
    private val getPrioritiesUseCase: GetPrioritiesUseCase,
    private val getPreclassifiersUseCase: GetPreclassifiersUseCase
) : SyncCatalogsUseCase {

    override suspend fun invoke() {
        val cardTypeList = getCardTypesUseCase()
        Log.e("List", "List cardTypes $cardTypeList")

        val preclassifierList = getPreclassifiersUseCase()
        Log.e("List", "List preclassifier $preclassifierList")

        val priorityList = getPrioritiesUseCase()
        Log.e("List","List priorities $priorityList")
    }
}