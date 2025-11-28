package com.ih.osm.domain.usecase.level

import com.ih.osm.core.utils.Paginator
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
            var localLevels = repo.getAll()

            if (syncRemote) {
                Paginator.fetchAll(
                    pageLimit = 1500,
                    batchSize = 500,
                    fetchPage = { p, l -> repo.getAllRemote(page = p, limit = l) },
                    saveBatch = { batch ->
                        val existingIds = localLevels.map { it.id }.toSet()
                        val newItems = batch.filter { it.id !in existingIds }
                        if (newItems.isNotEmpty()) {
                            repo.saveAll(newItems)
                            localLevels = localLevels + newItems
                        }
                    },
                )
            }
            return localLevels
        }
    }
