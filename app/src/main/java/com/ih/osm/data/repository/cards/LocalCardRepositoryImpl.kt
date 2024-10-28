package com.ih.osm.data.repository.cards

import com.ih.osm.data.database.dao.card.CardDao
import com.ih.osm.data.database.dao.solution.SolutionDao
import com.ih.osm.data.database.entities.card.toDomain
import com.ih.osm.data.database.entities.evidence.toDomain
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.cards.LocalCardRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.ui.utils.STORED_LOCAL
import javax.inject.Inject

class LocalCardRepositoryImpl @Inject constructor(
    private val dao: CardDao,
    private val solutionDao: SolutionDao,
    private val evidenceRepo: EvidenceRepository
) : LocalCardRepository {

    override suspend fun saveAll(cards: List<Card>) {
        cards.forEach {
            dao.insert(it.toEntity())
        }
    }

    override suspend fun getAll(): List<Card> {
        return dao.getAll().map {
            val hasLocalSolutions = solutionDao.getSolutions(it.uuid)
            it.toDomain(hasLocalSolutions = hasLocalSolutions.isNotEmpty())
        }.sortedByDescending { it.id }
    }

    override suspend fun getLastCardId(): String? {
        return dao.getLastCardId()
    }

    override suspend fun getLastSiteCardId(): Long? {
        return dao.getLastSiteCardId()
    }

    override suspend fun save(card: Card): Long {
        return dao.insert(card.toEntity())
    }

    override suspend fun getAllLocal(): List<Card> {
        val localCards = dao.getAllLocal(stored = STORED_LOCAL).toMutableList()
        val lists = solutionDao.getAllSolutions()
        lists.forEach {
            dao.get(it.cardId)?.let { card ->
                localCards.add(card)
            }
        }
        return localCards.toSet().map { cardEntity ->
            val evidences =
                evidenceRepo.getAllByCard(cardEntity.uuid)
            val hasLocalSolutions = solutionDao.getSolutions(cardEntity.uuid)
            cardEntity.toDomain(
                evidences = evidences,
                hasLocalSolutions = hasLocalSolutions.isNotEmpty()
            )
        }.sortedByDescending { it.siteCardId }
    }

    override suspend fun delete(uuid: String) {
        dao.delete(uuid)
    }

    override suspend fun get(uuid: String): Card? {
        val card = dao.get(uuid) ?: return null
        val evidences =
            evidenceRepo.getAllByCard(card.uuid)
        val hasLocalSolutions = solutionDao.getSolutions(card.uuid)

        return card.toDomain(
            evidences = evidences,
            hasLocalSolutions = hasLocalSolutions.isNotEmpty()
        )
    }

    override suspend fun getByZone(siteId: String, superiorId: String): List<Card> {
        return dao.getByZone(
            siteId,
            superiorId
        ).map { it.toDomain(hasLocalSolutions = false) }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }
}
