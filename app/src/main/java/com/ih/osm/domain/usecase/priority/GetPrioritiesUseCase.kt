package com.ih.osm.domain.usecase.priority

import android.util.Log
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.repository.priority.PriorityRepository
import javax.inject.Inject

interface GetPrioritiesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Priority>
}

class GetPrioritiesUseCaseImpl
    @Inject
    constructor(
        private val repo: PriorityRepository,
    ) : GetPrioritiesUseCase {
        override suspend fun invoke(syncRemote: Boolean): List<Priority> {
            Log.d("GetPrioritiesUseCase", "===== EXECUTE: syncRemote=$syncRemote =====")

            if (syncRemote) {
                Log.d("GetPrioritiesUseCase", "Syncing from remote...")
                val list = repo.getAllRemote()
                Log.d("GetPrioritiesUseCase", "Remote returned: ${list.size} priorities")
                repo.saveAll(list)
            }

            val result = repo.getAll()
            Log.d("GetPrioritiesUseCase", "SUCCESS: Returning ${result.size} priorities")
            return result
        }
    }
