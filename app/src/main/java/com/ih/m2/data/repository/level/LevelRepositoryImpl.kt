package com.ih.m2.data.repository.level

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Level
import com.ih.m2.domain.repository.level.LevelRepository
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