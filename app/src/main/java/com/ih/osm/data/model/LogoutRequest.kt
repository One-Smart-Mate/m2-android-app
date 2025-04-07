package com.ih.osm.data.model

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("osName")
    val osName: String,
)
