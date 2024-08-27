package com.osm.data.database.entities.cardtype

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.osm.domain.model.CardType
import com.osm.ui.extensions.defaultIfNull

@Entity(tableName = "card_type_table")
data class CardTypeEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "methodology")
    val methodology: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "color")
    val color: String,
    @ColumnInfo(name = "owner")
    val owner: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "quantity_images_create")
    val quantityImagesCreate: Int?,
    @ColumnInfo(name = "quantity_audios_create")
    val quantityAudiosCreate: Int?,
    @ColumnInfo(name = "quantity_videos_create")
    val quantityVideosCreate: Int?,
    @ColumnInfo(name = "audios_duration_create")
    val audiosDurationCreate: Int?,
    @ColumnInfo(name = "videos_duration_create")
    val videosDurationCreate: Int?,
    @ColumnInfo(name = "quantity_images_close")
    val quantityImagesClose: Int?,
    @ColumnInfo(name = "quantity_audios_close")
    val quantityAudiosClose: Int?,
    @ColumnInfo(name = "quantity_videos_close")
    val quantityVideosClose: Int?,
    @ColumnInfo(name = "audios_duration_close")
    val audiosDurationClose: Int?,
    @ColumnInfo(name = "videos_duration_close")
    val videosDurationClose: Int?,

    @ColumnInfo(name = "quantity_pictures_ps")
    val quantityImagesPs: Int?,
    @ColumnInfo(name = "quantity_audios_ps")
    val quantityAudiosPs: Int?,
    @ColumnInfo(name = "quantity_videos_ps")
    val quantityVideosPs: Int?,
    @ColumnInfo(name = "audios_duration_ps")
    val audiosDurationPs: Int?,
    @ColumnInfo(name = "videos_duration_ps")
    val videosDurationPs: Int?,
    @ColumnInfo(name = "card_type_methodology")
    val cardTypeMethodology: String?
)

fun CardTypeEntity.toDomain(): CardType {
    return CardType(
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
        videosDurationPs = this.videosDurationPs.defaultIfNull(0),
        cardTypeMethodology = this.cardTypeMethodology
    )
}
