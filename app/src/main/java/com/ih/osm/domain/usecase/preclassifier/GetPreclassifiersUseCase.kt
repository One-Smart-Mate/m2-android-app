package com.ih.osm.domain.usecase.preclassifier

import android.util.Log
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import javax.inject.Inject

interface GetPreclassifiersUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Preclassifier>
}

class GetPreclassifiersUseCaseImpl
    @Inject
    constructor(
        private val repo: PreclassifierRepository,
    ) : GetPreclassifiersUseCase {
        override suspend fun invoke(syncRemote: Boolean): List<Preclassifier> {
            Log.d("GetPreclassifiersUseCase", "===== EXECUTE: syncRemote=$syncRemote =====")

            if (syncRemote) {
                Log.d("GetPreclassifiersUseCase", "Syncing from remote...")
                val list = repo.getAllRemote()
                Log.d("GetPreclassifiersUseCase", "Remote returned: ${list.size} preclassifiers")
                repo.saveAll(list)
            }

            val result = repo.getAll()
            Log.d("GetPreclassifiersUseCase", "SUCCESS: Returning ${result.size} preclassifiers")
            return result
        }
    }
