package com.ih.osm.data.model

import com.ih.osm.domain.model.Sequence

data class GetSequenceResponse(val data: Sequence, val status: Int, val message: String)

fun GetSequenceResponse.toDomain(): Sequence {
    return this.data
}
