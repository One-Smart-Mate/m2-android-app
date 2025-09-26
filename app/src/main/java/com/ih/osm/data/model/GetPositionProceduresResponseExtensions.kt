package com.ih.osm.data.model

import com.ih.osm.domain.model.CiltProcedureData

fun GetPositionProceduresResponse.toDomain(): CiltProcedureData =
    try {
        // Use existing transformation directly - no filtering needed
        val tempResponse = GetCiltProcedureResponse(data = this.data)
        tempResponse.toDomain()
    } catch (e: Exception) {
        throw e
    }
