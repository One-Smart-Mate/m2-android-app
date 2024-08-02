package com.ih.m2.domain.usecase.card

import android.content.Context
import android.util.Log
import com.ih.m2.core.FileHelper
import com.ih.m2.core.network.NetworkConnection
import com.ih.m2.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.NetworkStatus
import com.ih.m2.domain.repository.cards.CardRepository
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.extensions.defaultIfNull
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface SaveCardUseCase {
    suspend operator fun invoke(card: Card): Long
}

class SaveCardUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val syncCardUseCase: SyncCardUseCase,
    @ApplicationContext private val context: Context
) : SaveCardUseCase {

    override suspend fun invoke(card: Card): Long {
        val lastCardId = localRepository.getLastCardId()
        val lastSiteCardId = localRepository.getLastSiteCardId()
        Log.e("test", "LastsiteCardId $lastSiteCardId")
        val user = localRepository.getUser()
        val cardType = localRepository.getCardType(card.cardTypeId)
        val area = localRepository.getLevel(card.areaId.toString())
        val priority = localRepository.getPriority(card.priorityId)
        val preclassifier = localRepository.getPreclassifier(card.preclassifierId)

        val updatedCard = card.copy(
            id = lastCardId.defaultIfNull("0").toLong().plus(1).toString(),
            siteCardId = lastSiteCardId.defaultIfNull(0).plus(1),
            siteId = user?.siteId,
            cardTypeColor = cardType?.color.orEmpty(),
            areaName = area?.name.orEmpty(),
            superiorId = area?.superiorId,
            priorityCode = priority?.code,
            priorityDescription = priority?.description,
            cardTypeMethodologyName = cardType?.methodology.orEmpty(),
            cardTypeName = cardType?.name,
            preclassifierCode = preclassifier?.code.orEmpty(),
            preclassifierDescription = preclassifier?.description.orEmpty(),
            creatorId = user?.userId,
            creatorName = user?.name.orEmpty(),
        )
        val id = localRepository.saveCard(updatedCard)
        card.evidences?.forEach {
            localRepository.saveEvidence(it)
        }

        firebaseAnalyticsHelper.logCreateCard(updatedCard)
        fileHelper.logCreateCard(updatedCard)
        Log.e("Card", "Card $updatedCard")
        return id
    }
}