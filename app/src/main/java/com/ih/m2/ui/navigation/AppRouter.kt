package com.ih.m2.ui.navigation

import com.ih.m2.ui.utils.EMPTY

const val ARG_CARD_ID = "arg_card_id"
const val ARG_SYNC_CATALOG = "arg_sync_catalogs"

private object Route {
    const val LOGIN = "login"
    const val HOME_PATH = "home"
    const val HOME = "$HOME_PATH?$ARG_SYNC_CATALOG={$ARG_SYNC_CATALOG}"
    const val ACCOUNT = "account"
    const val CARD_DETAIL_PATH = "card-detail"
    const val CARD_DETAIL = "$CARD_DETAIL_PATH/{${ARG_CARD_ID}}"
    const val CREATE_CARD = "create-card"
}

sealed class Screen(val route: String, val path: String = EMPTY) {
    data object Login: Screen(Route.LOGIN)
    data object Home: Screen(Route.HOME, Route.HOME_PATH)
    data object Account: Screen(Route.ACCOUNT)
    data object CardDetail: Screen(Route.CARD_DETAIL, Route.CARD_DETAIL_PATH)
    data object CreateCard: Screen(Route.CREATE_CARD)
}
