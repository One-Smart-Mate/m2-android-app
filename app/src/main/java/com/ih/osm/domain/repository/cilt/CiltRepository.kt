package com.ih.osm.domain.repository.cilt

import com.ih.osm.domain.model.CiltData

interface CiltRepository {
    suspend fun getCilts(): CiltData
}
