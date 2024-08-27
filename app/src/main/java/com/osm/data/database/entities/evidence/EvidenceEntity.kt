package com.osm.data.database.entities.evidence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.osm.domain.model.Evidence
import com.osm.ui.utils.EMPTY
import com.osm.ui.utils.STATUS_A


@Entity(tableName = "evidence_table")
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "card_id")
    val cardId: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "type")
    val type: String
)

fun EvidenceEntity.toDomain(): Evidence {
    return Evidence(
        id = this.id.toString(),
        cardId = this.cardId,
        url = this.url,
        type = this.type,
        createdAt = EMPTY,
        updatedAt = EMPTY,
        deletedAt = EMPTY,
        status = STATUS_A,
        siteId = EMPTY
    )
}
