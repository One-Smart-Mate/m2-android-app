package com.ih.osm.ui.navigation

import com.ih.osm.ui.utils.EMPTY

const val ARG_CARD_ID = "arg_card_id"
const val ARG_SYNC_CATALOG = "arg_sync_catalogs"
const val ARG_SOLUTION = "arg_solution"
const val ARG_CARD_FILTER = "arg_card_filter"
const val ARG_ACTION_TYPE = "arg_action_type"
const val ARG_SEQUENCE_ID = "sequenceId"

private object Route {
    const val LOGIN = "login"

//    const val HOME_PATH = "home"
//    const val HOME = "$HOME_PATH?$ARG_SYNC_CATALOG={$ARG_SYNC_CATALOG}"

    const val HOME_PATH_V2 = "home-v2"
    const val HOME_V2 = "$HOME_PATH_V2?$ARG_SYNC_CATALOG={$ARG_SYNC_CATALOG}"

    const val ACCOUNT = "account"
    const val CARD_DETAIL_PATH = "card-detail"
    const val CARD_DETAIL = "$CARD_DETAIL_PATH/{${ARG_CARD_ID}}"

    const val CREATE_CARD_PATH = "create-card"
    const val CREATE_CARD = "$CREATE_CARD_PATH/{$ARG_CARD_FILTER}"

    const val DEV = "dev"
    const val SOLUTION_PATH = "solution-card"
    const val SOLUTION = "$SOLUTION_PATH/{$ARG_ACTION_TYPE}/{$ARG_CARD_ID}"

    const val CARD_LIST_PATH = "card-list"
    const val CARD_LIST = "$CARD_LIST_PATH/{$ARG_CARD_FILTER}"

    const val PROFILE = "profile"
    const val QR_SCANNER = "qr-scanner"

    const val RESTORE_ACCOUNT = "restore-account"

    const val CILT_ROUTINE = "cilt-routine"
    const val CILT_DETAIL_PATH = "cilt-detail"
    const val CILT_DETAIL = "$CILT_DETAIL_PATH/{$ARG_SEQUENCE_ID}"

    const val OPL_LIST_PATH = "opl-list"
    const val OPL_LIST = OPL_LIST_PATH
}

sealed class Screen(val route: String, val path: String = EMPTY) {
    data object Login : Screen(Route.LOGIN)

    // data object Home : Screen(Route.HOME, Route.HOME_PATH)

    data object HomeV2 : Screen(Route.HOME_V2, Route.HOME_PATH_V2)

    data object Account : Screen(Route.ACCOUNT)

    data object CardDetail : Screen(Route.CARD_DETAIL, Route.CARD_DETAIL_PATH)

    data object CreateCard : Screen(Route.CREATE_CARD, Route.CREATE_CARD_PATH)

    data object Dev : Screen(Route.DEV)

    data object Solution : Screen(Route.SOLUTION, Route.SOLUTION_PATH)

    data object CardList : Screen(Route.CARD_LIST, Route.CARD_LIST_PATH)

    data object Profile : Screen(Route.PROFILE)

    data object QrScanner : Screen(Route.QR_SCANNER)

    data object RestoreAccount : Screen(Route.RESTORE_ACCOUNT)

    data object Cilt : Screen(Route.CILT_ROUTINE)

    data object CiltDetail : Screen(Route.CILT_DETAIL, Route.CILT_DETAIL_PATH) {
        fun createRoute(sequenceId: Int) = "$path/$sequenceId"
    }

    data object OplList : Screen(Route.OPL_LIST, Route.OPL_LIST_PATH)
}
