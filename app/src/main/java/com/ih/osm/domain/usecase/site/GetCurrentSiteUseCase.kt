package com.ih.osm.domain.usecase.site

import com.ih.osm.data.model.Site
import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

interface GetCurrentSiteUseCase {
    suspend operator fun invoke(): Site?
}

class GetCurrentSiteUseCaseImpl
    @Inject
    constructor(
        private val siteRepository: SiteRepository,
    ) : GetCurrentSiteUseCase {
        override suspend fun invoke(): Site? = siteRepository.getCurrentSite()
    }
