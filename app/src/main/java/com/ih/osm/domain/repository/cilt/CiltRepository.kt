package com.ih.osm.domain.repository.cilt

import com.ih.osm.data.model.UserCiltData

interface CiltRepository {
    suspend fun getUserCiltData(userId: String): UserCiltData
}
