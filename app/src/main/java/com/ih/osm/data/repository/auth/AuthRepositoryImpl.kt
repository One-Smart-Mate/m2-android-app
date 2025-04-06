package com.ih.osm.data.repository.auth

import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val dao: UserDao,
        private val networkRepository: NetworkRepository,
    ) : AuthRepository {
        override suspend fun login(data: LoginRequest): User {
            return networkRepository.login(data)
        }

        override suspend fun sendRestorePasswordCode(data: RestorePasswordRequest) {
            return networkRepository.sendRestorePasswordCode(data)
        }

        override suspend fun verifyPasswordCode(data: RestorePasswordRequest) {
            return networkRepository.verifyPasswordCode(data)
        }

        override suspend fun resetPassword(data: RestorePasswordRequest) {
            return networkRepository.resetPassword(data)
        }

        override suspend fun updateToken(data: UpdateTokenRequest) {
            return networkRepository.updateToken(data)
        }

        override suspend fun save(user: User): Long {
            return dao.insertUser(user.toEntity())
        }

        override suspend fun get(): User? {
            return dao.getUser().toDomain()
        }

        override suspend fun logout(): Int {
            val user = dao.getUser() ?: return 0
            networkRepository.logout(user.userId.toInt())
            return dao.deleteUser(user)
        }

        override suspend fun getSiteId(): String {
            return dao.getUser()?.siteId.orEmpty()
        }
    }
