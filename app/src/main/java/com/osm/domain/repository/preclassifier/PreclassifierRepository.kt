package com.osm.domain.repository.preclassifier

import com.osm.domain.model.Preclassifier

interface PreclassifierRepository {


    suspend fun getPreclassifiers(siteId: String): List<Preclassifier>
}