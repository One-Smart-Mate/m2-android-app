package com.ih.osm.domain.usecase.login

import com.ih.osm.data.model.LoginRequest
import com.ih.osm.data.model.LoginResponse
import com.ih.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface LoginUseCase {
    suspend operator fun invoke(data: LoginRequest): LoginResponse
}

class LoginUseCaseImpl
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : LoginUseCase {
        override suspend fun invoke(data: LoginRequest): LoginResponse {
            return authRepository.login(data)
        }
    }
