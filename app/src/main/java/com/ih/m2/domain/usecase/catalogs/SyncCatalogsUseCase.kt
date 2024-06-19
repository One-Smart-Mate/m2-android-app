package com.ih.m2.domain.usecase.catalogs

import android.util.Log
import com.ih.m2.domain.repository.cardtype.CardTypeRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.usecase.cardtype.GetCardTypesUseCase
import javax.inject.Inject

interface SyncCatalogsUseCase {
    suspend operator fun invoke()
}

class SyncCatalogsUseCaseImpl @Inject constructor(
    private val getCardTypesUseCase: GetCardTypesUseCase
) : SyncCatalogsUseCase {

    override suspend fun invoke() {
        val list = getCardTypesUseCase()
        Log.e("List","List $list")
    }
}