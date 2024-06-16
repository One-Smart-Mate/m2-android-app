package com.ih.m2.data.repository.local

import com.ih.m2.data.database.dao.UserDao
import com.ih.m2.data.database.entities.toDomain
import com.ih.m2.domain.model.User
import com.ih.m2.domain.model.toEntity
import com.ih.m2.domain.repository.local.LocalRepository
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
}