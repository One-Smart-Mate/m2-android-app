package com.ih.osm.ui.pages.home.action

import android.content.Context
import com.ih.osm.ui.utils.EMPTY

sealed class HomeAction {
    data class SyncCatalogs(val syncCatalogs: String = EMPTY) : HomeAction()

    data object GetCards : HomeAction()

    // data object SetIsSync : Action()

    data class SyncLocalCards(val context: Context) : HomeAction()

//    data object ClearMessage : Action()

    data object SyncRemoteCards : HomeAction()
}
