package com.ih.m2.domain.usecase.preclassifier

import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.repository.preclassifier.PreclassifierRepository
import javax.inject.Inject

interface GetPreclassifiersUseCase {
    suspend operator fun invoke(): List<Preclassifier>
}

class GetPreclassifiersUseCaseImpl @Inject constructor(
    private val preclassifierRepository: PreclassifierRepository,
    private val localRepository: LocalRepository
) : GetPreclassifiersUseCase {
    override suspend fun invoke(): List<Preclassifier> {
        val siteId = localRepository.getSiteId()
        return preclassifierRepository.getPreclassifiers(siteId)
    }
}