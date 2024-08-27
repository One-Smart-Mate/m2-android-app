package com.osm.data.repository.priority

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.osm.data.api.ApiService
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.Priority
import com.osm.domain.repository.priority.PriorityRepository
import javax.inject.Inject

class PriorityRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PriorityRepository {

    override suspend fun getPriorities(siteId: String): List<Priority> {
        val response = apiService.getPriorities(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}