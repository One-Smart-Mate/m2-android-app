package com.ih.osm.domain.repository.catalog

import com.ih.osm.domain.model.Catalogs

interface CatalogRepository {
    suspend fun getCatalogsBySite(siteId: String): Catalogs
}
