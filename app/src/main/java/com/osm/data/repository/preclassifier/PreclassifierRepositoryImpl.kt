package com.osm.data.repository.preclassifier

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.osm.data.api.ApiService
import com.osm.data.model.toDomain
import com.osm.data.repository.auth.getErrorMessage
import com.osm.domain.model.Preclassifier
import com.osm.domain.repository.preclassifier.PreclassifierRepository
import javax.inject.Inject

class PreclassifierRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): PreclassifierRepository {

    override suspend fun getPreclassifiers(siteId: String): List<Preclassifier> {
        val response = apiService.getPreclassifiers(siteId).execute()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.toDomain()
        } else {
            error(response.getErrorMessage())
        }
    }
}