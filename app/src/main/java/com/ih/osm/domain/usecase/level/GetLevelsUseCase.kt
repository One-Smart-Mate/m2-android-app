package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.model.Level
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

interface GetLevelsUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Level>
}

class GetLevelsUseCaseImpl
    @Inject
    constructor(
        private val repo: LevelRepository,
    ) : GetLevelsUseCase {
        override suspend fun invoke(syncRemote: Boolean): List<Level> {
            if (syncRemote) {
                val list = repo.getAllRemote()
                repo.saveAll(list)
            }
            return repo.getAll()
        }
    }
