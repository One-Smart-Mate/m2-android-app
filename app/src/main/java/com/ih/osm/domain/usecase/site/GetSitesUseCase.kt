package com.ih.osm.domain.usecase.site

import com.ih.osm.data.model.Site
import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

interface GetSitesUseCase {
    suspend operator fun invoke(): List<Site>
}

class GetSitesUseCaseImpl
    @Inject
    constructor(
        private val siteRepository: SiteRepository,
    ) : GetSitesUseCase {
        override suspend fun invoke(): List<Site> = siteRepository.getSites()
    }
