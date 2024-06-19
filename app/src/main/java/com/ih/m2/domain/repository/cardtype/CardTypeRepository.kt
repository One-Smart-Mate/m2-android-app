package com.ih.m2.domain.repository.cardtype

import com.ih.m2.domain.model.CardType

interface CardTypeRepository {

    suspend fun getCardTypes(siteId: String): List<CardType>
}