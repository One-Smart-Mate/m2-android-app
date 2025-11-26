package com.ih.osm.domain.usecase.site

import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

interface SetCurrentSiteUseCase {
    suspend operator fun invoke(siteId: String)
}

class SetCurrentSiteUseCaseImpl
    @Inject
    constructor(
        private val siteRepository: SiteRepository,
    ) : SetCurrentSiteUseCase {
        override suspend fun invoke(siteId: String) {
            siteRepository.setCurrentSite(siteId)
        }
    }
