package com.ih.osm.ui.pages.account.action

sealed class AccountAction {
    data object Logout: AccountAction()
    data object SyncCatalogs: AccountAction()
    data class SetSwitch(val checked: Boolean): AccountAction()
}