package com.ih.m2.domain.usecase.card

import android.util.Log
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.extensions.defaultIfNull
import javax.inject.Inject

interface SaveCardUseCase {
    suspend operator fun invoke(card: Card): Long
}

class SaveCardUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository,
) : SaveCardUseCase {

    override suspend fun invoke(card: Card): Long {
        Log.e("test","aquiii")
        val lastCardId = localRepository.getLastCardId()
        Log.e("test","aquiii 2- ${lastCardId.defaultIfNull("0").toLong().plus(1).toString()}")

        val lastSiteCardId = localRepository.getLastSiteCardId()
        Log.e("test","aquiii 8 - ${lastSiteCardId.defaultIfNull(0).plus(1)}")

        val user = localRepository.getUser()
        Log.e("test","aquiii 3 $user")

        val cardType = localRepository.getCardType(card.cardTypeId)
        Log.e("test","aquiii 4 $cardType")

        val area = localRepository.getLevel(card.areaId.toString())
        Log.e("test","aquiii 5 $area")

        val priority = localRepository.getPriority(card.priorityId)
        Log.e("test","aquiii 6 $priority")
        val preclassifier = localRepository.getPreclassifier(card.preclassifierId)
        Log.e("test","aquiii 7 $preclassifier")

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
        Log.e("Card","Card $updatedCard")
        return id
    }
}