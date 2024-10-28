package com.ih.osm.domain.usecase.level

import com.ih.osm.domain.model.Level
import com.ih.osm.domain.repository.level.LevelRepository
import com.ih.osm.domain.repository.level.LocalLevelRepository
import com.ih.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetLevelsUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Level>
}

class GetLevelsUseCaseImpl
@Inject
constructor(
    private val remoteRepo: LevelRepository,
    private val appLocalRepo: LocalRepository,
    private val localRepo: LocalLevelRepository
) : GetLevelsUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Level> {
        if (syncRemote) {
            val siteId = appLocalRepo.getSiteId()
            val list = remoteRepo.getLevels(siteId)
            localRepo.saveAll(list)
        }
        return localRepo.getAll()
    }
}
