package com.ih.osm.ui.pages.cardlist.action

import com.ih.osm.ui.components.card.actions.CardItemSheetAction

sealed class CardListAction {
    data class Detail(
        val id: String,
    ) : CardListAction()

    data object Create : CardListAction()

    data class Action(
        val action: CardItemSheetAction,
        val id: String,
    ) : CardListAction()

    data class Filters(
        val filter: String,
    ) : CardListAction()
}
