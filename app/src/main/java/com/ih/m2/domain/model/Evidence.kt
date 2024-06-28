package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.data.database.entities.evidence.EvidenceEntity
import com.ih.m2.ui.utils.AUDIO_CLOSE
import com.ih.m2.ui.utils.AUDIO_CREATION
import com.ih.m2.ui.utils.AUDIO_PS
import com.ih.m2.ui.utils.EMPTY
import com.ih.m2.ui.utils.IMG_CLOSE
import com.ih.m2.ui.utils.IMG_CREATION
import com.ih.m2.ui.utils.IMG_PS
import com.ih.m2.ui.utils.STATUS_A
import com.ih.m2.ui.utils.VIDEO_CLOSE
import com.ih.m2.ui.utils.VIDEO_CREATION
import com.ih.m2.ui.utils.VIDEO_PS
import java.util.UUID

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
) {
    companion object {
        fun fromCreateEvidence(cardId: String, url: String, type: String): Evidence {
            return Evidence(
                id = UUID.randomUUID().toString(),
                cardId = cardId,
                siteId = EMPTY,
                url = url,
                type = type,
                status = STATUS_A,
                createdAt = null,
                updatedAt = null,
                deletedAt = null
            )
        }
    }
}

fun Evidence.toEntity(): EvidenceEntity {
    return EvidenceEntity(
        cardId = this.cardId,
        url = this.url,
        type = this.type
    )
}

fun List<Evidence>.toImages(): List<Evidence> {
    return this.filter {
        it.type == IMG_CREATION || it.type == IMG_CLOSE || it.type == IMG_PS
    }
}

fun List<Evidence>.toVideos(): List<Evidence> {
    return this.filter {
        it.type == VIDEO_CLOSE || it.type == VIDEO_CREATION || it.type == VIDEO_PS
    }
}

fun List<Evidence>.toAudios(): List<Evidence> {
    return this.filter {
        it.type == AUDIO_CREATION || it.type == AUDIO_CLOSE || it.type == AUDIO_PS
    }
}

fun List<Evidence>.hasAudios(): Int {
    val result = this.any {
        it.type == AUDIO_CREATION
    }
    return if (result) 1 else 0
}

fun List<Evidence>.hasImages(): Int {
    val result = this.any {
        it.type == IMG_CREATION
    }
    return if (result) 1 else 0
}

fun List<Evidence>.hasVideos(): Int {
    val result = this.any {
        it.type == VIDEO_CREATION
    }
    return if (result) 1 else 0
}


