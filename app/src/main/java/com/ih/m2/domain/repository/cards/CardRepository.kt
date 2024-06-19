package com.ih.m2.domain.repository.cards

import com.ih.m2.domain.model.Card

interface CardRepository {

    suspend fun getCardsByUser(siteId: String): List<Card>

    suspend fun getCardDetail(cardId: String): Card
}