package com.ih.osm.domain.model

import com.ih.osm.ui.utils.EMPTY

data class NodeCardItem(
    val id: String = EMPTY,
    val name: String = EMPTY,
    val description: String = EMPTY,
    val superiorId: String = EMPTY
)
