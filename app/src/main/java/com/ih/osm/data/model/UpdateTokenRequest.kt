package com.ih.osm.data.model

import com.google.gson.annotations.SerializedName

data class UpdateTokenRequest(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("appToken")
    val appToken: String,
    @SerializedName("osName")
    val osName: String,
    @SerializedName("osVersion")
    val osVersion: String,
)
