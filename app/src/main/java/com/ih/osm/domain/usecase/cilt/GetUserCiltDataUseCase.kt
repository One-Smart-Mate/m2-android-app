package com.ih.osm.domain.usecase.cilt

import com.ih.osm.data.model.UserCiltData
import com.ih.osm.domain.repository.cilt.CiltRepository
import javax.inject.Inject

interface GetUserCiltDataUseCase {
    suspend operator fun invoke(userId: String): UserCiltData
}

class GetUserCiltDataUseCaseImpl
    @Inject
    constructor(
        private val repository: CiltRepository,
    ) : GetUserCiltDataUseCase {
        override suspend fun invoke(userId: String): UserCiltData {
            return repository.getUserCiltData(userId)
        }
    }
