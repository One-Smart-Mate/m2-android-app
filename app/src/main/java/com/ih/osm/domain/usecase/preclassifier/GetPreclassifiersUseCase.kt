package com.ih.osm.domain.usecase.preclassifier

import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.repository.local.LocalRepository
import com.ih.osm.domain.repository.preclassifier.LocalPreclassifierRepository
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
import javax.inject.Inject

interface GetPreclassifiersUseCase {
    suspend operator fun invoke(syncRemote: Boolean = false): List<Preclassifier>
}

class GetPreclassifiersUseCaseImpl
@Inject
constructor(
    private val remoteRepo: PreclassifierRepository,
    private val localRepo: LocalPreclassifierRepository,
    private val appLocalRepository: LocalRepository
) : GetPreclassifiersUseCase {
    override suspend fun invoke(syncRemote: Boolean): List<Preclassifier> {
        if (syncRemote) {
            val siteId = appLocalRepository.getSiteId()
            val list = remoteRepo.getPreclassifiers(siteId)
            localRepo.saveAll(list)
        }

        return localRepo.getAll()
    }
}
