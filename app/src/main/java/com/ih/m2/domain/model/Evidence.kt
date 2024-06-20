package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.ui.utils.AUDIO_CLOSE
import com.ih.m2.ui.utils.AUDIO_CREATION
import com.ih.m2.ui.utils.IMG_CLOSE
import com.ih.m2.ui.utils.IMG_CREATION
import com.ih.m2.ui.utils.VIDEO_CLOSE
import com.ih.m2.ui.utils.VIDEO_CREATION

data class Evidence(
    val id: String,
    val cardId: String,
    val siteId: String,
    @SerializedName("evidenceName")
    val url: String,
    @SerializedName("evidenceType")
    val type: String,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?,
    val deletedAt: String?
)

fun List<Evidence>.toImages(): List<Evidence> {
    return this.filter {
        it.type == IMG_CREATION || it.type == IMG_CLOSE
    }
}

fun List<Evidence>.toVideos(): List<Evidence> {
    return this.filter {
        it.type == VIDEO_CLOSE || it.type == VIDEO_CREATION
    }
}

fun List<Evidence>.toAudios(): List<Evidence> {
    return this.filter {
        it.type == AUDIO_CREATION || it.type == AUDIO_CLOSE
    }
}

