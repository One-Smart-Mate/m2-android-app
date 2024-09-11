package com.ih.osm.domain.repository.priority

import com.ih.osm.domain.model.Priority

interface PriorityRepository {

    suspend fun getPriorities(siteId: String): List<Priority>
}