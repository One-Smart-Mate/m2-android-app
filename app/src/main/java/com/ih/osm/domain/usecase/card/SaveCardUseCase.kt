package com.ih.osm.domain.usecase.card

import android.util.Log
import com.ih.osm.core.file.FileHelper
import com.ih.osm.core.notifications.NotificationManager
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.cardtype.CardTypeRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
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
    private val authRepo: AuthRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val fileHelper: FileHelper,
    private val cardRepo: CardRepository,
    private val cardTypeRepo: CardTypeRepository,
    private val preclassifierRepo: PreclassifierRepository,
    private val priorityRepo: PriorityRepository,
    private val levelRepo: LevelRepository,
    private val evidenceRepo: EvidenceRepository,
    private val notificationManager: NotificationManager
) : SaveCardUseCase {
    override suspend fun invoke(card: Card): Long {
        val lastCardId = cardRepo.getLastCardId()
        val lastSiteCardId = cardRepo.getLastSiteCardId()
        val user = authRepo.get()
        val cardType = cardTypeRepo.get(card.cardTypeId.orEmpty())
        val area = levelRepo.get(card.areaId.toString())
        val priority = priorityRepo.get(card.priorityId.orEmpty())
        val preclassifier = preclassifierRepo.get(card.preclassifierId)
        var uuid = card.uuid
        val hasData = cardRepo.get(uuid)
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
        val id = cardRepo.save(updatedCard)
        card.evidences?.forEach {
            evidenceRepo.save(it.copy(cardId = uuid))
        }
        firebaseAnalyticsHelper.logCreateCard(updatedCard)
        Log.e("Card", "Card $updatedCard")
        notificationManager.buildNotificationSuccessCard(updatedCard.siteCardId.toString())
        return id
    }
}
