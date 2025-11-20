package com.ih.osm.domain.usecase.catalogs

import com.ih.osm.domain.model.Catalogs
import com.ih.osm.domain.repository.catalog.CatalogRepository
import javax.inject.Inject

interface GetCatalogsBySiteUseCase {
    suspend operator fun invoke(siteId: String): Catalogs
}

class GetCatalogsBySiteUseCaseImpl
    @Inject
    constructor(
        private val catalogRepository: CatalogRepository,
    ) : GetCatalogsBySiteUseCase {
        override suspend fun invoke(siteId: String): Catalogs = catalogRepository.getCatalogsBySite(siteId)
    }
