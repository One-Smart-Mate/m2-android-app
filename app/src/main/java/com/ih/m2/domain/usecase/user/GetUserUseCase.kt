package com.ih.m2.domain.usecase.user

import com.ih.m2.domain.model.User
import com.ih.m2.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetUserUseCase  {
    suspend operator fun invoke(): User?
}

class GetUserUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
): GetUserUseCase {
    override suspend fun invoke(): User? {
        return localRepository.getUser()
    }
}