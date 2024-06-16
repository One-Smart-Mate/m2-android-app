package com.ih.m2.domain.usecase.login

import com.ih.m2.data.model.LoginRequest
import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.auth.AuthRepository
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