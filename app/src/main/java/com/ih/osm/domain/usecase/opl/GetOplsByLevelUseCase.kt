package com.ih.osm.domain.usecase.opl

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.repository.opl.OplRepository
import javax.inject.Inject

interface GetOplsByLevelUseCase {
    suspend operator fun invoke(levelId: String): List<Opl>
}

class GetOplsByLevelUseCaseImpl
    @Inject
    constructor(
        private val repo: OplRepository,
    ) : GetOplsByLevelUseCase {
        override suspend fun invoke(levelId: String): List<Opl> =
            if (NetworkConnection.isConnected()) {
                repo.getRemoteByLevel(levelId = levelId)
            } else {
                repo.getByLevel(levelId = levelId)
            }
    }
