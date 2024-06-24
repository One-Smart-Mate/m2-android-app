package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SaveCardUseCase {
    suspend operator fun invoke(card: Card): Long
}

class SaveCardUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
) : SaveCardUseCase {

    override suspend fun invoke(card: Card): Long {
        val lastCardId = localRepository.getLastCardId()
        val lastSiteCardId = localRepository.getLastSiteCardId()
        val user = localRepository.getUser()
        val cardType = localRepository.getCardType(card.cardTypeId.orEmpty())
        val area = localRepository.getLevel(card.areaId.toString())
        val priority = localRepository.getPriority(card.priorityId.orEmpty())
        val preclassifier = localRepository.getPreclassifier(card.preclassifierId)

        val updatedCard = card.copy(
            id = lastCardId.toLong().plus(1).toString(),
            siteCardId = lastSiteCardId.plus(1),
            siteId = user?.siteId,
            cardTypeColor = cardType.color,
            areaName = area.name,
            superiorId = area.superiorId,
            priorityCode = priority.code,
            priorityDescription = priority.description,
            cardTypeMethodologyName = cardType.methodology,
            cardTypeName = cardType.name,
            preclassifierCode = preclassifier.code,
            preclassifierDescription = preclassifier.description,
            creatorId = user?.userId,
            creatorName = user?.name.orEmpty(),
        )
        val id = localRepository.saveCard(updatedCard)
        card.evidences?.forEach {
            localRepository.saveEvidence(it)
        }
        Log.e("Card","Card $updatedCard")
        return id
    }
}