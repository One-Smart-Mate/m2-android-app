package com.ih.m2.data.repository.preclassifier

import com.ih.m2.data.api.ApiService
import com.ih.m2.data.model.toDomain
import com.ih.m2.data.repository.auth.getErrorMessage
import com.ih.m2.domain.model.Preclassifier
import com.ih.m2.domain.repository.preclassifier.PreclassifierRepository
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