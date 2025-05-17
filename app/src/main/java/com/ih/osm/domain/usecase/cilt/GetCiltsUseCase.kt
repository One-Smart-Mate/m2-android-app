package com.ih.osm.domain.usecase.cilt

import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface GetCiltsUseCase {
    suspend operator fun invoke(): CiltData
}

class GetCiltsUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : GetCiltsUseCase {
        override suspend fun invoke(): CiltData {
            return repository.getCilts()
        }
    }
