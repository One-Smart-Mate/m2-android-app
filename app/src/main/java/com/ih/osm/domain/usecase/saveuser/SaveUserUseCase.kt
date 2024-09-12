package com.ih.osm.domain.usecase.saveuser

import com.ih.osm.domain.model.User
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface SaveUserUseCase {
    suspend operator fun invoke(user: User): Long
}

class SaveUserUseCaseImpl
    @Inject
    constructor(
        private val localRepository: LocalRepository,
    ) : SaveUserUseCase {
        override suspend fun invoke(user: User): Long {
            return localRepository.saveUser(user)
        }
    }
