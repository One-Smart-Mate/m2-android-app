package com.osm.domain.usecase.level


import android.util.Log
import com.osm.domain.model.Level
import com.osm.domain.repository.level.LevelRepository
import com.osm.domain.repository.local.LocalRepository
import javax.inject.Inject

interface GetLevelsUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Level>
}

class GetLevelsUseCaseImpl @Inject constructor(
    private val levelRepository: LevelRepository,
    private val localRepository: LocalRepository
) : GetLevelsUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Level> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val levelList = levelRepository.getLevels(siteId)
            Log.e("test", "Level List $levelList")
            localRepository.saveLevels(levelList)
        }
        return localRepository.getLevels()
    }
}