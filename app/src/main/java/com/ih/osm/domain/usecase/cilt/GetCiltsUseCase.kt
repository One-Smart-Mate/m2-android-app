package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.GetCiltsRequest
import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface GetCiltsUseCase {
    suspend operator fun invoke(body: GetCiltsRequest): CiltData
}

class GetCiltsUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : GetCiltsUseCase {
        override suspend fun invoke(body: GetCiltsRequest): CiltData {
            return repository.getCilts(body)
        }
    }
