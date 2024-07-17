package com.ih.m2.domain.usecase.password

import com.ih.m2.data.model.RestorePasswordRequest
import com.ih.m2.domain.repository.auth.AuthRepository
import javax.inject.Inject

interface ResetPasswordUseCase  {
    suspend operator fun invoke(request: RestorePasswordRequest)
}

class ResetPasswordUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
): ResetPasswordUseCase {
    override suspend fun invoke(request: RestorePasswordRequest) {
        authRepository.resetPassword(request)
    }
}