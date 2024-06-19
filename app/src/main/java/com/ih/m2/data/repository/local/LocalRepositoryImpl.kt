package com.ih.m2.data.repository.local

import android.util.Log
import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.dao.card.CardDao
import com.ih.m2.data.database.dao.cardtype.CardTypeDao
import com.ih.m2.data.database.dao.preclassifier.PreclassifierDao
import com.ih.m2.data.database.dao.priority.PriorityDao
import com.ih.m2.data.database.entities.UserEntity
import com.ih.m2.data.database.entities.card.toDomain
import com.ih.m2.data.database.entities.cardtype.toDomain
import com.ih.m2.data.database.entities.preclassifier.toDomain
import com.ih.m2.data.database.entities.priority.toDomain
import com.ih.m2.data.database.entities.toDomain
import com.ih.m2.domain.model.Card
import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.model.Priority
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.toEntity
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.utils.EMPTY
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cardDao: CardDao,
    private val cardTypeDao: CardTypeDao,
    private val preclassifierDao: PreclassifierDao,
    private val priorityDao: PriorityDao
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
            Log.e("card","Card ${it.id} ${it.stored}")
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
            Log.e("card","Card Type ${it.id} ${it.name}")
        }
    }


    override suspend fun getPreclassifiers(): List<Preclassifier> {
        return preclassifierDao.getPreclassifiers().map { it.toDomain() }
    }

    override suspend fun savePreclassifiers(list: List<Preclassifier>) {
        preclassifierDao.deletePreclassifiers()
        list.forEach {
            Log.e("card","Card Type ${it.id} ${it.description}")
        }
    }

    override suspend fun getPriorities(): List<Priority> {
        return priorityDao.getPriorities().map { it.toDomain() }
    }

    override suspend fun savePriorities(list: List<Priority>) {
        priorityDao.deletePriorities()
        list.forEach {
            Log.e("card","Card Type ${it.id} ${it.description}")
        }
    }

}