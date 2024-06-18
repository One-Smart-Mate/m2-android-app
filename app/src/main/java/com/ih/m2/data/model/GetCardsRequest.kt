package com.ih.m2.data.model

import com.google.gson.annotations.SerializedName

data class GetCardsRequest(
    @SerializedName("responsibleId")
    val userId: String
)