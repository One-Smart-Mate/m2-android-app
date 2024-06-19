package com.ih.m2.domain.usecase.priority

import com.ih.m2.domain.model.Priority
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.repository.priority.PriorityRepository
import javax.inject.Inject

interface GetPrioritiesUseCase {
    suspend operator fun invoke(): List<Priority>
}

class GetPrioritiesUseCaseImpl @Inject constructor(
    private val priorityRepository: PriorityRepository,
    private val localRepository: LocalRepository
) : GetPrioritiesUseCase {
    override suspend fun invoke(): List<Priority> {
        val siteId = localRepository.getSiteId()
        return priorityRepository.getPriorities(siteId)
    }
}