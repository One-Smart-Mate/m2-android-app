package com.ih.osm.domain.repository.level

import com.ih.osm.domain.model.Level

interface LocalLevelRepository {

    suspend fun saveAll(list: List<Level>)

    suspend fun getAll(): List<Level>

    suspend fun get(id: String): Level?

    suspend fun deleteAll()
}