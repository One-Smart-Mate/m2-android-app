package com.ih.osm.domain.usecase.login

import com.ih.osm.data.model.FastLoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface FastLoginUseCase {
    suspend fun invoke(body: FastLoginRequest): LoginResponse
}

class FastLoginUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : FastLoginUseCase {
        override suspend fun invoke(body: FastLoginRequest): LoginResponse {
            return authRepository.fastLogin(body)
        }
    }
