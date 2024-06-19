package com.ih.m2.domain.repository.local

import com.ih.m2.data.database.entities.UserEntity
import com.ih.m2.domain.model.User

interface LocalRepository {
    suspend fun saveUser(user: User): Long
    suspend fun getUser(): User?
    suspend fun logout(): Int
    suspend fun getSiteId(): String
}