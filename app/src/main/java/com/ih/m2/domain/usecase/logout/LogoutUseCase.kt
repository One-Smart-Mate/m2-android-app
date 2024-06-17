package com.ih.m2.domain.usecase.logout

import com.ih.m2.data.model.LoginRequest
import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.auth.AuthRepository
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface LogoutUseCase {
    suspend operator fun invoke(): Int
}

class LogoutUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
) : LogoutUseCase {
    override suspend fun invoke(): Int {
        return localRepository.logout()
    }
}