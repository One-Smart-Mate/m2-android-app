package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName

data class CardType (
    val id: String,
    val methodology: String,
    val name: String,
    val description: String,
    val color: String,
    @SerializedName("responsableName")
    val owner: String,
    val status: String
)
