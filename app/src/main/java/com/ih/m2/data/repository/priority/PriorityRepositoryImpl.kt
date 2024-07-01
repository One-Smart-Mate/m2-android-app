package com.ih.m2.data.repository.priority

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Priority
import com.ih.m2.domain.repository.priority.PriorityRepository
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