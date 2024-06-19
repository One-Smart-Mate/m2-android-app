package com.ih.m2.data.repository.local

import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.entities.UserEntity
import com.ih.m2.data.database.entities.toDomain
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.toEntity
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.ui.utils.EMPTY
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
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