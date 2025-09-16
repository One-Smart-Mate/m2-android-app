package com.ih.osm.ui.pages.account.action

import android.net.Uri

sealed class AccountAction {
    data object Logout : AccountAction()

    data object SyncCatalogs : AccountAction()

    data class SetSwitch(
        val checked: Boolean,
    ) : AccountAction()

    data class UploadLogs(
        val uri: Uri,
    ) : AccountAction()
}
