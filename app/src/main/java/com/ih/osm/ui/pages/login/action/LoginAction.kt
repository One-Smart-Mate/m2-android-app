package com.ih.osm.ui.pages.login.action

sealed class LoginAction {
    data object Login : LoginAction()

    data class SetEmail(
        val email: String,
    ) : LoginAction()

    data class SetPassword(
        val password: String,
    ) : LoginAction()
}
