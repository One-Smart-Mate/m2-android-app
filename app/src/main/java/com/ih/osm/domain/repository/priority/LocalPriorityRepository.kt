package com.ih.osm.domain.repository.priority

import com.ih.osm.domain.model.Priority

interface LocalPriorityRepository {
    suspend fun getAll(): List<Priority>

    suspend fun saveAll(list: List<Priority>)

    suspend fun get(id: String): Priority?

    suspend fun deleteAll()
}
