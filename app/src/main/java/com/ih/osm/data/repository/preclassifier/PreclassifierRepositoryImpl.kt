package com.ih.osm.data.repository.preclassifier

import com.ih.osm.data.api.ApiService
import com.ih.osm.data.model.toDomain
import com.ih.osm.data.repository.auth.getErrorMessage
import com.ih.osm.domain.model.Preclassifier
import com.ih.osm.domain.repository.preclassifier.PreclassifierRepository
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