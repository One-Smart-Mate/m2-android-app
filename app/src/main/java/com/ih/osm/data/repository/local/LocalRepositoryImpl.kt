package com.ih.osm.data.repository.local

import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.database.entities.card.toDomain
import com.ih.osm.data.database.entities.cardtype.toDomain
import com.ih.osm.data.database.entities.employee.toDomain
import com.ih.osm.data.database.entities.evidence.toDomain
import com.ih.osm.data.database.entities.level.toDomain
import com.ih.osm.data.database.entities.preclassifier.toDomain
import com.ih.osm.data.database.entities.priority.toDomain
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl
@Inject
constructor(
    private val userDao: UserDao
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
}
