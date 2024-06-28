package com.ih.m2.domain.repository.local

import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Employee
import com.ih.m2.domain.model.Evidence
import com.ih.m2.domain.model.Level
import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.model.Priority
import com.ih.m2.domain.model.User

interface LocalRepository {
    suspend fun saveUser(user: User): Long
    suspend fun getUser(): User?
    suspend fun logout(): Int
    suspend fun getSiteId(): String

    suspend fun saveCards(list: List<Card>)
    suspend fun getCards(): List<Card>
    suspend fun getLastCardId(): String
    suspend fun getLastSiteCardId(): Long
    suspend fun saveCard(card: Card): Long
    suspend fun getLocalCards(): List<Card>
    suspend fun deleteCard(id: String)
    suspend fun getCard(cardId: String): Card
    suspend fun getCardsZone(superiorId: String): List<Card>

    suspend fun getCardTypes(): List<CardType>
    suspend fun saveCardTypes(list: List<CardType>)
    suspend fun getCardType(id: String): CardType

    suspend fun getPreclassifiers(): List<Preclassifier>
    suspend fun savePreclassifiers(list: List<Preclassifier>)
    suspend fun getPreclassifier(id: String): Preclassifier

    suspend fun getPriorities(): List<Priority>
    suspend fun savePriorities(list: List<Priority>)
    suspend fun getPriority(id: String): Priority

    suspend fun saveLevels(list: List<Level>)
    suspend fun getLevels(): List<Level>
    suspend fun getLevel(id: String): Level

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