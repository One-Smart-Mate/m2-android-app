package com.ih.m2.data.repository.local

import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.dao.card.CardDao
import com.ih.m2.data.database.dao.cardtype.CardTypeDao
import com.ih.m2.data.database.dao.evidence.EvidenceDao
import com.ih.m2.data.database.dao.level.LevelDao
import com.ih.m2.data.database.dao.preclassifier.PreclassifierDao
import com.ih.m2.data.database.dao.priority.PriorityDao
import com.ih.m2.data.database.entities.card.toDomain
import com.ih.m2.data.database.entities.cardtype.toDomain
import com.ih.m2.data.database.entities.level.toDomain
import com.ih.m2.data.database.entities.preclassifier.toDomain
import com.ih.m2.data.database.entities.priority.toDomain
import com.ih.m2.data.database.entities.toDomain
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.Level
import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.model.Priority
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.toEntity
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cardDao: CardDao,
    private val cardTypeDao: CardTypeDao,
    private val preclassifierDao: PreclassifierDao,
    private val priorityDao: PriorityDao,
    private val levelDao: LevelDao,
    private val evidenceDao: EvidenceDao
) : LocalRepository {

    override suspend fun saveUser(user: User): Long {
        return userDao.insertUser(user.toEntity())
    }

    override suspend fun getUser(): User? {
        return userDao.getUser().toDomain()
    }

    override suspend fun logout(): Int {
        userDao.getUser()?.let {
            return userDao.deleteUser(it)
        }
        return 0
    }

    override suspend fun getSiteId(): String {
        return userDao.getUser()?.siteId.orEmpty()
    }

    override suspend fun saveCards(list: List<Card>) {
        cardDao.deleteCards()
        list.forEach {
            cardDao.insertCard(it.toEntity())
        }
    }

    override suspend fun getCards(): List<Card> {
        return cardDao.getCards().map { it.toDomain() }
    }

    override suspend fun getCardTypes(): List<CardType> {
        return cardTypeDao.getCardTypes().map { it.toDomain() }
    }

    override suspend fun saveCardTypes(list: List<CardType>) {
        cardTypeDao.deleteCardTypes()
        list.forEach {
            cardTypeDao.insertCardType(it.toEntity())
        }
    }


    override suspend fun getPreclassifiers(): List<Preclassifier> {
        return preclassifierDao.getPreclassifiers().map { it.toDomain() }
    }

    override suspend fun savePreclassifiers(list: List<Preclassifier>) {
        preclassifierDao.deletePreclassifiers()
        list.forEach {
            preclassifierDao.insertPreclassifier(it.toEntity())
        }
    }

    override suspend fun getPriorities(): List<Priority> {
        return priorityDao.getPriorities().map { it.toDomain() }
    }

    override suspend fun savePriorities(list: List<Priority>) {
        priorityDao.deletePriorities()
        list.forEach {
            priorityDao.insertPriority(it.toEntity())
        }
    }

    override suspend fun removeCardTypes() {
        cardTypeDao.deleteCardTypes()
    }

    override suspend fun removeCards() {
        cardDao.deleteCards()
    }

    override suspend fun removePreclassifiers() {
        preclassifierDao.deletePreclassifiers()
    }

    override suspend fun removePriorities() {
        priorityDao.deletePriorities()
    }

    override suspend fun saveLevels(list: List<Level>) {
        levelDao.deleteLevels()
        list.forEach {
            levelDao.insertLevel(it.toEntity())
        }
    }

    override suspend fun getLevels(): List<Level> {
        return levelDao.getLevels().map { it.toDomain() }
    }

    override suspend fun removeLevels() {
        levelDao.deleteLevels()
    }

    override suspend fun getLastCardId(): String {
        return cardDao.getLastCardId()
    }

    override suspend fun getLastSiteCardId(): Long {
        return cardDao.getLastSiteCardId()
    }

    override suspend fun getCardType(id: String): CardType {
        return cardTypeDao.getCardType(id).toDomain()
    }

    override suspend fun getPreclassifier(id: String): Preclassifier {
        return preclassifierDao.getPreclassifier(id).toDomain()
    }

    override suspend fun getPriority(id: String): Priority {
        return priorityDao.getPriority(id).toDomain()
    }

    override suspend fun getLevel(id: String): Level {
        return levelDao.getLevel(id).toDomain()
    }

    override suspend fun saveCard(card: Card): Long {
        return cardDao.insertCard(card.toEntity())
    }

    override suspend fun saveEvidence(evidence: Evidence): Long {
        return evidenceDao.insertEvidence(evidence.toEntity())
    }

}