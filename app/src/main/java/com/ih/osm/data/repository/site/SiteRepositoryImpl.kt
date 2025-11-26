package com.ih.osm.data.repository.site

import com.ih.osm.data.database.dao.site.SiteDao
import com.ih.osm.data.database.entities.site.toDomain
import com.ih.osm.data.database.entities.site.toEntity
import com.ih.osm.data.model.Site
import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

class SiteRepositoryImpl
    @Inject
    constructor(
        private val siteDao: SiteDao,
    ) : SiteRepository {
        override suspend fun getSites(): List<Site> = siteDao.getSites().map { it.toDomain() }

        override suspend fun getCurrentSite(): Site? = siteDao.getCurrentSite()?.toDomain()

        override suspend fun saveSites(sites: List<Site>) {
            siteDao.clearSites()
            siteDao.insertSites(sites.map { it.toEntity() })
        }

        override suspend fun setCurrentSite(siteId: String) {
            siteDao.clearCurrentSite()
            siteDao.setCurrentSite(siteId)
        }

        override suspend fun clearSites() {
            siteDao.clearSites()
        }
    }
