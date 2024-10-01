package com.ih.osm.domain.usecase.card

import android.content.Context
import android.util.Log
import com.ih.osm.core.file.FileHelper
import com.ih.osm.data.repository.firebase.FirebaseAnalyticsHelper
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.ui.extensions.defaultIfNull
import com.ih.osm.ui.utils.STORED_LOCAL
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val syncCardUseCase: SyncCardUseCase,
    @ApplicationContext private val context: Context
) : SaveCardUseCase {
    override suspend fun invoke(card: Card): Long {
        val lastCardId = localRepository.getLastCardId()
        val lastSiteCardId = localRepository.getLastSiteCardId()
        val user = localRepository.getUser()
        val cardType = localRepository.getCardType(card.cardTypeId)
        val area = localRepository.getLevel(card.areaId.toString())
        val priority = localRepository.getPriority(card.priorityId)
        val preclassifier = localRepository.getPreclassifier(card.preclassifierId)
        var uuid = UUID.randomUUID().toString()
        val hasData = localRepository.getCardByUUID(uuid)
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
        val id = localRepository.saveCard(updatedCard)
        card.evidences?.forEach {
            localRepository.saveEvidence(it)
        }
        firebaseAnalyticsHelper.logCreateCard(updatedCard)
        Log.e("Card", "Card $updatedCard")
        return id
    }
}
