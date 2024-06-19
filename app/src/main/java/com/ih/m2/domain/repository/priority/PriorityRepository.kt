package com.ih.m2.domain.repository.priority

import com.ih.m2.domain.model.Priority

interface PriorityRepository {

    suspend fun getPriorities(siteId: String): List<Priority>
}