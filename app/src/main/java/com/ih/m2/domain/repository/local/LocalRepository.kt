package com.ih.m2.domain.repository.local

import com.ih.m2.data.database.entities.UserEntity
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
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
    suspend fun getCardTypes(): List<CardType>
    suspend fun saveCardTypes(list: List<CardType>)
    suspend fun getPreclassifiers(): List<Preclassifier>
    suspend fun savePreclassifiers(list: List<Preclassifier>)
    suspend fun getPriorities(): List<Priority>
    suspend fun savePriorities(list: List<Priority>)

    suspend fun removeCards()
    suspend fun removeCardTypes()
    suspend fun removePreclassifiers()
    suspend fun removePriorities()
}