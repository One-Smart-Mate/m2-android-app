package com.ih.osm.domain.repository.site

import com.ih.osm.data.model.Site

interface SiteRepository {
    suspend fun getSites(): List<Site>

    suspend fun getCurrentSite(): Site?

    suspend fun saveSites(sites: List<Site>)

    suspend fun setCurrentSite(siteId: String)

    suspend fun clearSites()
}
