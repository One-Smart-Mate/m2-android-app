package com.osm.domain.usecase.preclassifier

import com.osm.domain.model.Preclassifier
import com.osm.domain.repository.local.LocalRepository
import com.osm.domain.repository.preclassifier.PreclassifierRepository
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