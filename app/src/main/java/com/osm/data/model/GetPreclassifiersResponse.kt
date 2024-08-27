package com.osm.data.model

import com.osm.domain.model.Preclassifier


data class GetPreclassifiersResponse (
    val data: List<Preclassifier>,
    val status: Long,
    val message: String
)

fun GetPreclassifiersResponse.toDomain() = this.data
