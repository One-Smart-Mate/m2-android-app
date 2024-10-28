package com.ih.osm.data.repository.level

import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.repository.network.getErrorMessage
import com.ih.osm.domain.model.Level
import com.ih.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

class LevelRepositoryImpl
@Inject
constructor(
    private val apiService: ApiService
) : LevelRepository {
    override suspend fun getLevels(siteId: String): List<Level> {
        val response = apiService.getLevels(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}
