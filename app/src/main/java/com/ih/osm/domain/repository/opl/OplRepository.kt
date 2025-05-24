package com.ih.osm.domain.repository.opl

import com.ih.osm.domain.model.Opl

interface OplRepository {
    suspend fun getRemoteByLevel(levelId: String): List<Opl>

    suspend fun getByLevel(levelId: String): List<Opl>

    suspend fun saveAll(opls: List<Opl>)

    suspend fun getAll(): List<Opl>

    suspend fun deleteAll()
}
