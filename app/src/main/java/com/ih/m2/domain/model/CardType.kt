package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity
import com.ih.m2.data.database.entities.cardtype.toDomain
import com.ih.m2.ui.extensions.defaultIfNull
import com.ih.m2.ui.utils.CARD_MAINTENANCE

data class CardType(
    val id: String,
    val methodology: String,
    val name: String,
    val description: String,
    val color: String,
    @SerializedName("responsableName")
    val owner: String,
    val status: String,
    @SerializedName("quantityPicturesCreate")
    val quantityImagesCreate: Int?,
    @SerializedName("quantityAudiosCreate")
    val quantityAudiosCreate: Int?,
    @SerializedName("quantityVideosCreate")
    val quantityVideosCreate: Int?,
    @SerializedName("audiosDurationCreate")
    val audiosDurationCreate: Int?,
    @SerializedName("videosDurationCreate")
    val videosDurationCreate: Int?,
    @SerializedName("quantityPicturesClose")
    val quantityImagesClose: Int?,
    @SerializedName("quantityAudiosClose")
    val quantityAudiosClose: Int?,
    @SerializedName("quantityVideosClose")
    val quantityVideosClose: Int?,
    @SerializedName("audiosDurationClose")
    val audiosDurationClose: Int?,
    @SerializedName("videosDurationClose")
    val videosDurationClose: Int?,
    @SerializedName("quantityPicturesPs")
    val quantityImagesPs: Int?,
    @SerializedName("quantityAudiosPs")
    val quantityAudiosPs: Int?,
    @SerializedName("quantityVideosPs")
    val quantityVideosPs: Int?,
    @SerializedName("audiosDurationPs")
    val audiosDurationPs: Int?,
    @SerializedName("videosDurationPs")
    val videosDurationPs: Int?
)


fun CardType.toEntity(): CardTypeEntity {
    return CardTypeEntity(
        id = this.id,
        methodology = this.methodology,
        name = this.name,
        description = this.description,
        color = this.color,
        owner = this.owner,
        status = this.status,
        quantityImagesCreate = this.quantityImagesCreate.defaultIfNull(0),
        quantityAudiosCreate = this.quantityAudiosCreate.defaultIfNull(0),
        quantityVideosCreate = this.quantityVideosCreate.defaultIfNull(0),
        audiosDurationCreate = this.audiosDurationCreate.defaultIfNull(0),
        videosDurationCreate = this.videosDurationCreate.defaultIfNull(0),
        quantityImagesClose = this.quantityImagesClose.defaultIfNull(0),
        quantityAudiosClose = this.quantityAudiosClose.defaultIfNull(0),
        quantityVideosClose = this.quantityVideosClose.defaultIfNull(0),
        audiosDurationClose = this.audiosDurationClose.defaultIfNull(0),
        videosDurationClose = this.videosDurationClose.defaultIfNull(0),
        quantityImagesPs = this.quantityImagesPs.defaultIfNull(0),
        quantityAudiosPs = this.quantityAudiosPs.defaultIfNull(0),
        quantityVideosPs = this.quantityVideosPs.defaultIfNull(0),
        audiosDurationPs = this.audiosDurationPs.defaultIfNull(0),
        videosDurationPs = this.videosDurationPs.defaultIfNull(0)
    )
}



fun List<CardType>.toNodeItemList(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(id = it.id, name = it.methodology, description = it.name)
    }
}

fun CardType.isBehavior() = this.methodology == "C" ||
        this.name == "Comportamiento" ||
        this.methodology == "Comportamiento" ||
        this.name == "C"

fun NodeCardItem?.isMaintenanceCardType(): Boolean = this?.name?.lowercase() == CARD_MAINTENANCE.lowercase()

fun NodeCardItem?.isBehaviorCardType(): Boolean = this?.name?.lowercase() == "Comportamiento".lowercase() || this?.name?.lowercase() == "C".lowercase()