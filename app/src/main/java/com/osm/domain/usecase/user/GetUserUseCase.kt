package com.osm.domain.usecase.user

import com.osm.domain.model.User
import com.osm.domain.repository.local.LocalRepository
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