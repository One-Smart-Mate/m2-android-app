package com.ih.osm.domain.usecase.login

import com.ih.osm.data.model.LoginResponse
import com.ih.osm.data.model.RefreshTokenRequest
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface RefreshTokenUseCase {
    suspend operator fun invoke(body: RefreshTokenRequest): LoginResponse
}

class RefreshTokenUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : RefreshTokenUseCase {
        override suspend fun invoke(body: RefreshTokenRequest): LoginResponse = authRepository.refreshToken(body)
    }
