package com.osm.data.repository.level

import com.osm.data.api.ApiService
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.Level
import com.osm.domain.repository.level.LevelRepository
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): LevelRepository {

    override suspend fun getLevels(siteId: String): List<Level> {
        val response = apiService.getLevels(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}