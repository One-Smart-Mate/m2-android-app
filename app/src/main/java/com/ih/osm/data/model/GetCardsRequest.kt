package com.ih.osm.data.model

import com.google.gson.annotations.SerializedName

data class GetCardsRequest(
    @SerializedName("responsibleId")
    val userId: String
)