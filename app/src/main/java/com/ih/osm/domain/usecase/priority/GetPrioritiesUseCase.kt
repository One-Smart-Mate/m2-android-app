package com.ih.osm.domain.usecase.priority

import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.repository.auth.AuthRepository
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
    private val authRepository: AuthRepository,
    private val localRepo: LocalPriorityRepository
) : GetPrioritiesUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Priority> {
        if (syncRemote) {
            val siteId = authRepository.getSiteId()
            val list = remoteRepo.getPriorities(siteId)
            localRepo.saveAll(list)
        }
        return localRepo.getAll()
    }
}
