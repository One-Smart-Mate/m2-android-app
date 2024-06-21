package com.ih.m2.data.model

import com.ih.m2.domain.model.Level

data class GetLevelsResponse(val data: List<Level>, val status: Int, val message: String)

fun GetLevelsResponse.toDomain() = this.data