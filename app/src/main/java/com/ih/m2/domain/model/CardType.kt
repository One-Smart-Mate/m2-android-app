package com.ih.m2.domain.model

import com.google.gson.annotations.SerializedName
import com.ih.m2.data.database.entities.cardtype.CardTypeEntity
import com.ih.m2.ui.utils.CARD_MAINTENANCE

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


fun CardType.toEntity(): CardTypeEntity {
    return CardTypeEntity(
        id = this.id,
        methodology = this.methodology,
        name = this.name,
        description = this.description,
        color = this.color,
        owner = this.owner,
        status = this.status
    )
}


fun List<CardType>.toNodeItemList(): List<NodeCardItem> {
    return this.map {
        NodeCardItem(id = it.id, name = it.methodology, description = it.name)
    }
}

fun CardType.isMaintenance() = this.name == CARD_MAINTENANCE
fun NodeCardItem?.isMaintenanceCardType(): Boolean = this?.name == CARD_MAINTENANCE