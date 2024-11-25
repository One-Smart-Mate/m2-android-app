package com.ih.osm.ui.pages.password.action

sealed class RestoreAction {
    data class SetEmail(val email: String) : RestoreAction()

    data class SetAction(val action: String) : RestoreAction()

    data class SetCode(val code: String) : RestoreAction()

    data class SetPassword(val password: String) : RestoreAction()

    data class ConfirmPassword(val password: String) : RestoreAction()
}
