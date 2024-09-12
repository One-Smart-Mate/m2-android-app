package com.ih.osm.domain.repository.local

import com.ih.osm.domain.model.Card
import com.ih.osm.domain.model.CardType
import com.ih.osm.domain.model.Employee
import com.ih.osm.domain.model.Evidence
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.model.User
import com.ih.osm.ui.utils.EMPTY

interface LocalRepository {
    suspend fun saveUser(user: User): Long

    suspend fun getUser(): User?

    suspend fun logout(): Int

    suspend fun getSiteId(): String

    suspend fun saveCards(list: List<Card>)

    suspend fun getCards(): List<Card>

    suspend fun getLastCardId(): String?

    suspend fun getLastSiteCardId(): Long?

    suspend fun saveCard(card: Card): Long

    suspend fun getLocalCards(): List<Card>

    suspend fun deleteCard(id: String)

    suspend fun getCard(cardId: String): Card

    suspend fun getCardsZone(
        siteId: String,
        superiorId: String,
    ): List<Card>

    suspend fun getCardTypes(filter: String = EMPTY): List<CardType>

    suspend fun saveCardTypes(list: List<CardType>)

    suspend fun getCardType(id: String?): CardType?

    suspend fun getPreclassifiers(): List<Preclassifier>

    suspend fun savePreclassifiers(list: List<Preclassifier>)

    suspend fun getPreclassifier(id: String?): Preclassifier?

    suspend fun getPriorities(): List<Priority>

    suspend fun savePriorities(list: List<Priority>)

    suspend fun getPriority(id: String?): Priority?

    suspend fun saveLevels(list: List<Level>)

    suspend fun getLevels(): List<Level>

    suspend fun getLevel(id: String?): Level?

    suspend fun removeLevels()

    suspend fun removeCards()

    suspend fun removeCardTypes()

    suspend fun removePreclassifiers()

    suspend fun removePriorities()

    suspend fun saveEvidence(evidence: Evidence): Long

    suspend fun deleteEvidence(id: String)

    suspend fun deleteEvidences()

    suspend fun saveEmployees(list: List<Employee>)

    suspend fun deleteEmployees()

    suspend fun getEmployees(): List<Employee>
}
