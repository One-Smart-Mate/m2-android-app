package com.ih.osm.domain.usecase.preclassifier

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
            if (syncRemote) {
                val list = repo.getAllRemote()
                repo.saveAll(list)
            }

            return repo.getAll()
        }
    }
