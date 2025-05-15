package com.ih.osm.data.repository.cilt

import com.ih.osm.data.model.UserCiltData
import com.ih.osm.domain.repository.cilt.CiltRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

data class CiltRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) : CiltRepository {
        override suspend fun getUserCiltData(userId: String): UserCiltData {
            return networkRepository.getUserCiltData(userId)
        }
    }
