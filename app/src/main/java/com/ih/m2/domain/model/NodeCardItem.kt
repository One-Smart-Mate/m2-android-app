package com.ih.m2.domain.model

import com.ih.m2.ui.utils.EMPTY

data class NodeCardItem(
    val id: String = EMPTY,
    val name: String = EMPTY,
    val description: String = EMPTY,
    val superiorId: String = EMPTY
)