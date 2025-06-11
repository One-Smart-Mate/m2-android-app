package com.ih.osm.domain.usecase.cilt

import com.ih.osm.domain.model.Opl
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface GetOplByIdUseCase {
    suspend operator fun invoke(id: String): Opl
}

class GetOplByIdUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : GetOplByIdUseCase {
        override suspend fun invoke(id: String): Opl {
            return repository.getOplById(id)
        }
    }
