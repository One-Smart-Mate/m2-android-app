package com.ih.m2.domain.usecase.preclassifier

import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.repository.local.LocalRepository
import com.ih.m2.domain.repository.preclassifier.PreclassifierRepository
import javax.inject.Inject

interface GetPreclassifiersUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Preclassifier>
}

class GetPreclassifiersUseCaseImpl @Inject constructor(
    private val preclassifierRepository: PreclassifierRepository,
    private val localRepository: LocalRepository
) : GetPreclassifiersUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Preclassifier> {
        if (syncRemote) {
            val siteId = localRepository.getSiteId()
            val preclassifierList = preclassifierRepository.getPreclassifiers(siteId)
            localRepository.savePreclassifiers(preclassifierList)
        }
        return localRepository.getPreclassifiers()
    }
}