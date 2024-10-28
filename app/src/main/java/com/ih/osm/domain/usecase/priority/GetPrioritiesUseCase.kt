package com.ih.osm.domain.usecase.priority

import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.repository.priority.LocalPriorityRepository
import com.ih.osm.domain.repository.priority.PriorityRepository
import javax.inject.Inject

interface GetPrioritiesUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Priority>
}

class GetPrioritiesUseCaseImpl
@Inject
constructor(
    private val remoteRepo: PriorityRepository,
    private val localRepository: LocalRepository,
    private val localRepo: LocalPriorityRepository
) : GetPrioritiesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Priority> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val list = remoteRepo.getPriorities(siteId)
            localRepo.saveAll(list)
        }
        return localRepo.getAll()
    }
}
