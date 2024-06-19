package com.ih.m2.ui.navigation

const val ARG_CARD_ID = "arg_card_id"

private object Route {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ACCOUNT = "account"
    const val CARD_DETAIL = "card-detail/{${ARG_CARD_ID}}"
    const val CREATE_CARD = "create-card"
}

sealed class Screen(val route: String) {
    data object Login: Screen(Route.LOGIN)
    data object Home: Screen(Route.HOME)
    data object Account: Screen(Route.ACCOUNT)
    data object CardDetail: Screen(Route.CARD_DETAIL)
    data object CreateCard: Screen(Route.CREATE_CARD)

}