package com.osm.domain.usecase.login

import com.osm.data.model.LoginRequest
import com.osm.domain.model.User
import com.osm.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface LoginUseCase {
    suspend operator fun invoke(data: LoginRequest): User
}

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): LoginUseCase {
    override suspend fun invoke(data: LoginRequest): User {
        return authRepository.login(data)
    }
}