package com.osm.domain.usecase.saveuser

import com.osm.domain.model.User
import com.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SaveUserUseCase  {
    suspend operator fun invoke(user: User): Long
}

class SaveUserUseCaseImpl @Inject constructor(
    private val localRepository: LocalRepository
): SaveUserUseCase {
    override suspend fun invoke(user: User): Long {
        return localRepository.saveUser(user)
    }
}