package com.ih.osm.domain.usecase.card

import android.util.Log
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.cardtype.LocalCardTypeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.level.LocalLevelRepository
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.repository.preclassifier.LocalPreclassifierRepository
import com.ih.osm.domain.repository.priority.LocalPriorityRepository
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.STORED_LOCAL
import java.util.UUID
import javax.inject.Inject

interface SaveCardUseCase {
    suspend operator fun invoke(card: Card): Long
}

class SaveCardUseCaseImpl
@Inject
constructor(
    private val localRepository: LocalRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val localCardRepo: LocalCardRepository,
    private val localCardTypeRepo: LocalCardTypeRepository,
    private val localPreclassifierRepo: LocalPreclassifierRepository,
    private val localPriorityRepo: LocalPriorityRepository,
    private val localLevelRepo: LocalLevelRepository,
    private val evidenceRepo: EvidenceRepository
) : SaveCardUseCase {
    override suspend fun invoke(card: Card): Long {
        val lastCardId = localCardRepo.getLastCardId()
        val lastSiteCardId = localCardRepo.getLastSiteCardId()
        val user = localRepository.getUser()
        val cardType = localCardTypeRepo.get(card.cardTypeId.orEmpty())
        val area = localLevelRepo.get(card.areaId.toString())
        val priority = localPriorityRepo.get(card.priorityId.orEmpty())
        val preclassifier = localPreclassifierRepo.get(card.preclassifierId)
        var uuid = card.uuid
        val hasData = localCardRepo.get(uuid)
        if (hasData != null) {
            uuid = UUID.randomUUID().toString()
        }
        Log.e("test", "IDS -> $lastSiteCardId -- $lastCardId --- $uuid")
        val updatedCard =
            card.copy(
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
                stored = STORED_LOCAL,
                uuid = uuid
            )
        fileHelper.logCreateCard(updatedCard)
        val id = localCardRepo.save(updatedCard)
        card.evidences?.forEach {
            evidenceRepo.save(it.copy(cardId = uuid))
        }
        firebaseAnalyticsHelper.logCreateCard(updatedCard)
        Log.e("Card", "Card $updatedCard")
        return id
    }
}
