package com.ih.osm.data.repository.catalog

import com.ih.osm.domain.model.Catalogs
import com.ih.osm.domain.repository.catalog.CatalogRepository
import com.ih.osm.domain.repository.network.NetworkRepository
import javax.inject.Inject

class CatalogRepositoryImpl
    @Inject
    constructor(
        private val networkRepository: NetworkRepository,
    ) : CatalogRepository {
        override suspend fun getCatalogsBySite(siteId: String): Catalogs = networkRepository.getCatalogsBySite(siteId)
    }
