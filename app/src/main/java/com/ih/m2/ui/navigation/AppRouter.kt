package com.ih.m2.ui.navigation

private object Route {
    const val LOGIN = "login"
    const val HOME = "home"
}

sealed class Screen(val route: String) {
    data object Login: Screen(Route.LOGIN)
    data object Home: Screen(Route.HOME)
}