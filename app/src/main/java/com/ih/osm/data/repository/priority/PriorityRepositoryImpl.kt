package com.ih.osm.data.repository.priority

import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.repository.auth.getErrorMessage
import com.ih.osm.domain.model.Priority
import com.ih.osm.domain.repository.priority.PriorityRepository
import javax.inject.Inject

class PriorityRepositoryImpl
    @Inject
    constructor(
        private val apiService: ApiService,
    ) : PriorityRepository {
        override suspend fun getPriorities(siteId: String): List<Priority> {
            val response = apiService.getPriorities(siteId).execute()
            return if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                error(response.getErrorMessage())
            }
        }
    }
