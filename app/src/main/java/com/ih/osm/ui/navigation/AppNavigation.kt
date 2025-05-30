package com.ih.osm.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ih.osm.ui.components.card.actions.CardItemSheetAction
import com.ih.osm.ui.components.card.actions.toActionString
import com.ih.osm.ui.pages.account.AccountScreen
import com.ih.osm.ui.pages.cardaction.CardActionScreen
import com.ih.osm.ui.pages.carddetail.CardDetailScreen
import com.ih.osm.ui.pages.cardlist.CardListScreen
import com.ih.osm.ui.pages.cilt.CiltDetailScreen
import com.ih.osm.ui.pages.cilt.CiltScreen
import com.ih.osm.ui.pages.createcard.CreateCardScreen
import com.ih.osm.ui.pages.dev.DevScreen
import com.ih.osm.ui.pages.home.HomeScreenV2
import com.ih.osm.ui.pages.login.LoginScreen
import com.ih.osm.ui.pages.opllist.OplListScreen
import com.ih.osm.ui.pages.password.RestoreAccountScreen
import com.ih.osm.ui.pages.profile.ProfileScreen
import com.ih.osm.ui.pages.qr.QrScannerScreen
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700))
        },
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(
            Screen.HomeV2.route,
            arguments =
            listOf(
                navArgument(ARG_SYNC_CATALOG) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = EMPTY
                },
            ),
        ) {
            HomeScreenV2(navController = navController)
        }
        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }
        composable(
            Screen.CardDetail.route,
            arguments = listOf(navArgument(ARG_CARD_ID) { type = NavType.StringType }),
        ) {
            CardDetailScreen(navController = navController)
        }
        composable(
            Screen.CreateCard.route,
            arguments = listOf(navArgument(ARG_CARD_FILTER) { type = NavType.StringType }),
        ) {
            CreateCardScreen(navController = navController)
        }
        composable(Screen.Dev.route) {
            DevScreen(navController)
        }
        composable(
            Screen.Solution.route,
            arguments =
            listOf(
                navArgument(ARG_CARD_ID) { type = NavType.StringType },
                navArgument(ARG_ACTION_TYPE) { type = NavType.StringType },
            ),
        ) {
            CardActionScreen(navController = navController)
        }

        composable(
            Screen.CardList.route,
            arguments = listOf(navArgument(ARG_CARD_FILTER) { type = NavType.StringType }),
        ) {
            CardListScreen(navController = navController)
        }

        composable(
            Screen.Profile.route,
        ) {
            ProfileScreen(navController = navController)
        }

        composable(
            Screen.QrScanner.route,
        ) {
            QrScannerScreen(navController = navController)
        }

        composable(
            Screen.RestoreAccount.route,
        ) {
            RestoreAccountScreen(navController = navController)
        }

        composable(
            route = Screen.Cilt.route,
        ) {
            CiltScreen(navController = navController)
        }

        composable(
            route = Screen.CiltDetail.route,
            arguments =
            listOf(
                navArgument("sequenceId") {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val sequenceId = backStackEntry.arguments?.getInt("sequenceId") ?: 0
            CiltDetailScreen(sequenceId = sequenceId, navController = navController)
        }

        composable(
            Screen.OplList.route,
        ) {
            OplListScreen(navController = navController)
        }
    }
}

fun NavController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}

// fun NavController.navigateToHome() {
//    navigateAndClean("${Screen.Home.path}?$ARG_SYNC_CATALOG=$LOAD_CATALOGS")
// }

fun NavController.navigateToHomeV2() {
    navigateAndClean("${Screen.HomeV2.path}?$ARG_SYNC_CATALOG=$LOAD_CATALOGS")
}

fun NavController.navigateToLogin() {
    navigateAndClean(Screen.Login.route)
}

fun NavController.navigateToAccount() {
    navigate(Screen.Account.route)
}

fun NavController.navigateToCardDetail(uuid: String) {
    navigate(
        "${Screen.CardDetail.path}/$uuid",
    )
}

fun NavController.navigateToCardSolution(
    action: CardItemSheetAction,
    uuid: String,
) {
    navigate(
        "${Screen.Solution.path}/${action.toActionString()}/$uuid",
    )
}

fun NavController.navigateToCardList(filter: String) {
    navigate(
        "${Screen.CardList.path}/$filter",
    )
}

fun NavController.navigateToCreateCard(filter: String = EMPTY) {
    navigate("${Screen.CreateCard.path}/$filter")
}

fun NavController.navigateToProfile() {
    navigate(Screen.Profile.route)
}

fun NavController.navigateToQrScanner() {
    navigate(Screen.QrScanner.route)
}

fun NavController.navigateToRestoreAccount() {
    navigate(Screen.RestoreAccount.route)
}

fun NavController.navigateToCiltRoutine() {
    navigate(Screen.Cilt.route)
}

fun NavController.navigateToCiltDetail(sequenceId: Int) {
    navigate(Screen.CiltDetail.createRoute(sequenceId))
}

fun NavController.navigateToOplList() {
    navigate(Screen.OplList.route)
}
