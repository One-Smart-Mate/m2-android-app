package com.ih.osm.domain.usecase.site

import com.ih.osm.domain.repository.site.SiteRepository
import javax.inject.Inject

interface ClearSitesUseCase {
    suspend operator fun invoke()
}

class ClearSitesUseCaseImpl
    @Inject
    constructor(
        private val siteRepository: SiteRepository,
    ) : ClearSitesUseCase {
        override suspend fun invoke() {
            siteRepository.clearSites()
        }
    }
