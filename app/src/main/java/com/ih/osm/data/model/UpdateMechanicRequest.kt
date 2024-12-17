package com.ih.osm.data.model

data class UpdateMechanicRequest(
    val cardId: Int,
    val mechanicId: Int,
    val idOfUpdatedBy: Int,
)
