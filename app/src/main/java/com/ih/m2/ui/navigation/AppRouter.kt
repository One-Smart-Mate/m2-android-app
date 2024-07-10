package com.ih.m2.ui.navigation

import com.ih.m2.ui.utils.EMPTY

const val ARG_CARD_ID = "arg_card_id"
const val ARG_SYNC_CATALOG = "arg_sync_catalogs"
const val ARG_SOLUTION = "arg_solution"
const val ARG_CARD_FILTER = "arg_card_filter"

private object Route {
    const val LOGIN = "login"
    const val HOME_PATH = "home"
    const val HOME = "$HOME_PATH?$ARG_SYNC_CATALOG={$ARG_SYNC_CATALOG}"

    const val HOME_PATH_V2 = "home-v2"
    const val HOME_V2 = "$HOME_PATH_V2?$ARG_SYNC_CATALOG={$ARG_SYNC_CATALOG}"

    const val ACCOUNT = "account"
    const val CARD_DETAIL_PATH = "card-detail"
    const val CARD_DETAIL = "$CARD_DETAIL_PATH/{${ARG_CARD_ID}}"
    const val CREATE_CARD = "create-card"
    const val DEV = "dev"
    const val SOLUTION_PATH = "solution-card"
    const val SOLUTION  = "$SOLUTION_PATH/{$ARG_SOLUTION}/{$ARG_CARD_ID}"

    const val CARD_LIST_PATH = "card-list"
    const val CARD_LIST  = "$CARD_LIST_PATH/{$ARG_CARD_FILTER}"
}

sealed class Screen(val route: String, val path: String = EMPTY) {
    data object Login: Screen(Route.LOGIN)
    data object Home: Screen(Route.HOME, Route.HOME_PATH)
    data object HomeV2: Screen(Route.HOME_V2, Route.HOME_PATH_V2)
    data object Account: Screen(Route.ACCOUNT)
    data object CardDetail: Screen(Route.CARD_DETAIL, Route.CARD_DETAIL_PATH)
    data object CreateCard: Screen(Route.CREATE_CARD)
    data object Dev: Screen(Route.DEV)
    data object Solution: Screen(Route.SOLUTION, Route.SOLUTION_PATH)
    data object CardList: Screen(Route.CARD_LIST, Route.CARD_LIST_PATH)
}
