package com.ih.m2.data.model

import com.ih.m2.domain.model.Priority


data class GetPrioritiesResponse(
    val data: List<Priority>,
    val status: Long,
    val message: String
)

fun GetPrioritiesResponse.toDomain() = this.data