package com.ih.osm.domain.repository.preclassifier

import com.ih.osm.domain.model.Preclassifier

interface LocalPreclassifierRepository {

    suspend fun getAll(): List<Preclassifier>

    suspend fun saveAll(list: List<Preclassifier>)

    suspend fun get(id: String): Preclassifier?

    suspend fun deleteAll()
}
