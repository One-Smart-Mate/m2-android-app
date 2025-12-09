package com.ih.osm.domain.usecase.card

import com.ih.osm.core.network.NetworkConnection
import com.ih.osm.data.model.GetPaginatedCardsResponse
import com.ih.osm.domain.repository.cards.CardRepository
import javax.inject.Inject

interface GetAllPagedCardsUseCase {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 1500,
        syncRemote: Boolean = false,
    ): GetPaginatedCardsResponse
}

class GetAllPagedCardsUseCaseImpl
    @Inject
    constructor(
        private val cardRepository: CardRepository,
    ) : GetAllPagedCardsUseCase {
        override suspend fun invoke(
            page: Int,
            limit: Int,
            syncRemote: Boolean,
        ): GetPaginatedCardsResponse =
            if (syncRemote && NetworkConnection.isConnected()) {
                val remote =
                    cardRepository.getAllRemoteByUser(
                        page = page,
                        limit = limit,
                    )

                cardRepository.saveAll(remote.data)

                GetPaginatedCardsResponse(
                    data = remote.data,
                    page = remote.page ?: page,
                    limit = remote.limit ?: limit,
                    totalPages = remote.totalPages ?: 1,
                    hasMore = remote.hasMore,
                )
            } else {
                val totalCount = cardRepository.getCount()
                val totalPages =
                    if (totalCount == 0) {
                        1
                    } else {
                        ((totalCount + limit - 1) / limit)
                    }

                val offset = (page - 1) * limit
                val localPage =
                    cardRepository.getPaged(
                        offset = offset,
                        limit = limit,
                    )

                GetPaginatedCardsResponse(
                    data = localPage,
                    page = page,
                    limit = limit,
                    totalPages = totalPages,
                    hasMore = page < totalPages,
                )
            }
    }
