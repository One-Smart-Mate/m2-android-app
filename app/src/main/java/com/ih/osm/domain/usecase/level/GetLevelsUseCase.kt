package com.ih.osm.domain.usecase.level

import android.util.Log
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
            Log.d("GetLevelsUseCase", "===== EXECUTE: syncRemote=$syncRemote =====")

            if (syncRemote) {
                Log.d("GetLevelsUseCase", "Syncing from remote...")
                val list = repo.getAllRemote()
                Log.d("GetLevelsUseCase", "Remote returned: ${list.size} levels")
                repo.saveAll(list)
            }

            val result = repo.getAll()
            Log.d("GetLevelsUseCase", "SUCCESS: Returning ${result.size} levels")
            return result
        }
    }
