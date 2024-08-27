package com.osm.domain.repository.priority

import com.osm.domain.model.Priority

interface PriorityRepository {

    suspend fun getPriorities(siteId: String): List<Priority>
}