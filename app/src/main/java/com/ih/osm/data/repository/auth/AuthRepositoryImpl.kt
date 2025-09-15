package com.ih.osm.data.repository.auth

import com.ih.osm.data.database.dao.UserDao
import com.ih.osm.data.database.entities.toDomain
import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.LogoutRequest
import com.ih.osm.data.model.RefreshTokenRequest
import com.ih.osm.data.model.RestorePasswordRequest
import com.ih.osm.data.model.SendFastPasswordRequest
import com.ih.osm.data.model.SendFastPasswordResponse
import com.ih.osm.data.model.UpdateTokenRequest
import com.ih.osm.data.model.toDomain
import com.ih.osm.domain.model.User
import com.ih.osm.domain.model.toEntity
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import com.ih.osm.ui.utils.ANDROID_SO
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val dao: UserDao,
        private val networkRepository: NetworkRepository,
    ) : AuthRepository {
        override suspend fun login(data: LoginRequest): LoginResponse = networkRepository.login(data)

        override suspend fun sendRestorePasswordCode(data: RestorePasswordRequest) = networkRepository.sendRestorePasswordCode(data)

        override suspend fun verifyPasswordCode(data: RestorePasswordRequest) = networkRepository.verifyPasswordCode(data)

        override suspend fun resetPassword(data: RestorePasswordRequest) = networkRepository.resetPassword(data)

        override suspend fun updateToken(data: UpdateTokenRequest) = networkRepository.updateToken(data)

        override suspend fun save(user: User): Long = dao.insertUser(user.toEntity())

        override suspend fun get(): User? = dao.getUser().toDomain()

        override suspend fun logout(): Int {
            val user = dao.getUser() ?: return 0
            networkRepository.logout(
                LogoutRequest(user.userId.toInt(), ANDROID_SO.uppercase()),
            )
            return dao.deleteUser(user)
        }

        override suspend fun getSiteId(): String = dao.getUser()?.siteId.orEmpty()

        override suspend fun fastLogin(body: FastLoginRequest): LoginResponse = networkRepository.fastLogin(body)

        override suspend fun sendFastPassword(body: SendFastPasswordRequest): SendFastPasswordResponse =
            networkRepository.sendFastPassword(body)

        override suspend fun refreshToken(body: RefreshTokenRequest): LoginResponse = networkRepository.refreshToken(body)
    }
