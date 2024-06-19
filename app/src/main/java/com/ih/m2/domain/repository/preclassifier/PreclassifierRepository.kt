package com.ih.m2.domain.repository.preclassifier

import com.ih.m2.domain.model.CardType
import com.ih.m2.domain.model.Preclassifier

interface PreclassifierRepository {


    suspend fun getPreclassifiers(siteId: String): List<Preclassifier>
}