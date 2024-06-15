package com.ih.m2.domain.usecase

import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.AuthRepository
import javax.inject.Inject

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): User
}

class LoginUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): LoginUseCase {
    override suspend fun invoke(email: String, password: String): User {
        return authRepository.login(email, password)
    }
}