package com.ih.osm.data.repository.cilt

import com.ih.osm.domain.model.CiltData
import com.ih.osm.domain.repository.auth.AuthRepository
import com.ih.osm.domain.repository.cilt.CiltRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

data class CiltRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
        private val authRepo: AuthRepository,
    ) : CiltRepository {
        override suspend fun getCilts(): CiltData {
            val userId = authRepo.get()?.userId.orEmpty()
            return networkRepository.getCilts(userId)
        }
    }
