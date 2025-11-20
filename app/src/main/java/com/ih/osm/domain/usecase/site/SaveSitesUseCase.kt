package com.ih.osm.domain.usecase.site

import com.ih.osm.data.model.Site
import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

interface SaveSitesUseCase {
    suspend operator fun invoke(sites: List<Site>)
}

class SaveSitesUseCaseImpl
    @Inject
    constructor(
        private val siteRepository: SiteRepository,
    ) : SaveSitesUseCase {
        override suspend fun invoke(sites: List<Site>) {
            siteRepository.saveSites(sites)
        }
    }
