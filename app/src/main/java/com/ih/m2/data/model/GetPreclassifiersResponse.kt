package com.ih.m2.data.model

import com.ih.m2.domain.model.Preclassifier


data class GetPreclassifiersResponse (
    val data: List<Preclassifier>,
    val status: Long,
    val message: String
)

fun GetPreclassifiersResponse.toDomain() = this.data
