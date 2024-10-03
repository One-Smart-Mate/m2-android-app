package com.ih.osm.data.model

import com.ih.osm.domain.model.Preclassifier

data class GetPreclassifiersResponse(
    val data: List<Preclassifier>,
    val status: Long,
    val message: String
)

fun GetPreclassifiersResponse.toDomain() = this.data
