package com.ih.osm.data.repository.cards

import com.ih.osm.core.app.LoggerHelperManager
import com.ih.osm.data.database.dao.card.CardDao
import com.ih.osm.data.database.entities.card.toDomain
import com.ih.osm.data.model.CreateCardRequest
import com.ih.osm.data.model.UpdateMechanicRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cards.CardRepository
import com.ih.osm.domain.repository.evidence.EvidenceRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.domain.repository.solution.SolutionRepository
import com.ih.osm.ui.utils.STORED_LOCAL
import javax.inject.Inject

class CardRepositoryImpl
    @Inject
    constructor(
        private val dao: CardDao,
        private val networkRepository: NetworkRepository,
        private val solutionRepo: SolutionRepository,
        private val evidenceRepo: EvidenceRepository,
        private val authRepo: AuthRepository,
    ) : CardRepository {
        override suspend fun saveAll(cards: List<Card>) {
            try {
                cards.forEach {
                    dao.insert(it.toEntity())
                }
            } catch (e: Exception) {
                LoggerHelperManager.logException(e)
            }
        }

        override suspend fun getAll(): List<Card> {
            val cards =
                dao
                    .getAll()
                    .map {
                        val hasLocalSolutions = solutionRepo.getAllByCard(it.uuid)
                        it.toDomain(hasLocalSolutions = hasLocalSolutions.isNotEmpty())
                    }.sortedBy { it.id }
            return cards
        }

        override suspend fun getLastCardId(): String? = dao.getLastCardId()

        override suspend fun getLastSiteCardId(): Long? = dao.getLastSiteCardId()

        override suspend fun save(card: Card): Long = dao.insert(card.toEntity())

        override suspend fun getAllLocal(): List<Card> {
            val localCards = dao.getAllLocal(stored = STORED_LOCAL).toMutableList()
            val lists = solutionRepo.getAll()
            lists.forEach {
                dao.get(it.cardId)?.let { card ->
                    localCards.add(card)
                }
            }
            val sortedCards =
                localCards
                    .toSet()
                    .map { cardEntity ->
                        val evidences =
                            evidenceRepo.getAllByCard(cardEntity.uuid)
                        val hasLocalSolutions = solutionRepo.getAllByCard(cardEntity.uuid)
                        cardEntity.toDomain(
                            evidences = evidences,
                            hasLocalSolutions = hasLocalSolutions.isNotEmpty(),
                        )
                    }.sortedBy { it.siteCardId }
            return sortedCards
        }

        override suspend fun delete(uuid: String) {
            dao.delete(uuid)
        }

        override suspend fun get(uuid: String): Card? {
            val card = dao.get(uuid) ?: return null
            val evidences =
                evidenceRepo.getAllByCard(card.uuid)
            val hasLocalSolutions = solutionRepo.getAllByCard(card.uuid)

            return card.toDomain(
                evidences = evidences,
                hasLocalSolutions = hasLocalSolutions.isNotEmpty(),
            )
        }

        override suspend fun getByZone(superiorId: String): List<Card> {
            val siteId = authRepo.getSiteId()
            return dao
                .getByZone(
                    siteId,
                    superiorId,
                ).map { it.toDomain(hasLocalSolutions = false) }
        }

        override suspend fun deleteAll() {
            dao.deleteAll()
        }

        override suspend fun getAllRemoteByUser(): List<Card> {
            val siteId = authRepo.getSiteId()
            return networkRepository.getRemoteCardsByUser(siteId)
        }

        override suspend fun getRemote(cardId: String): Card? = networkRepository.getRemoteCardDetail(cardId)

        override suspend fun saveRemote(card: CreateCardRequest): Card = networkRepository.saveRemoteCard(card)

        override suspend fun getRemoteByZone(superiorId: String): List<Card> {
            val siteId = authRepo.getSiteId()
            return networkRepository.getRemoteCardsZone(superiorId, siteId)
        }

        override suspend fun updateRemoteMechanic(body: UpdateMechanicRequest) = networkRepository.updateRemoteMechanic(body)

        override suspend fun getRemoteByLevelMachine(levelMachine: String): List<Card> {
            val siteId = authRepo.getSiteId()
            return networkRepository.getRemoteCardsLevelMachine(levelMachine, siteId)
        }
    }
