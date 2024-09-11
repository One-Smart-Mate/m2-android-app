package com.ih.osm.domain.usecase.login

import com.ih.osm.data.model.LoginRequest
import com.ih.osm.domain.model.User
import com.ih.osm.domain.repository.auth.AuthRepository
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