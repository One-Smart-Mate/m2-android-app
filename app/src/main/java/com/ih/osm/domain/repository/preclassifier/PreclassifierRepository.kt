package com.ih.osm.domain.repository.preclassifier

import com.ih.osm.domain.model.Preclassifier

interface PreclassifierRepository {


    suspend fun getPreclassifiers(siteId: String): List<Preclassifier>
}