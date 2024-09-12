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
import com.ih.osm.ui.pages.account.AccountScreen
import com.ih.osm.ui.pages.carddetail.CardDetailScreen
import com.ih.osm.ui.pages.cardlist.CardListScreen
import com.ih.osm.ui.pages.createcard.CreateCardScreen
import com.ih.osm.ui.pages.dev.DevScreen
import com.ih.osm.ui.pages.home.HomeScreenV2
import com.ih.osm.ui.pages.login.LoginScreen
import com.ih.osm.ui.pages.password.RestoreAccountScreen
import com.ih.osm.ui.pages.profile.ProfileScreen
import com.ih.osm.ui.pages.qr.QrScannerScreen
import com.ih.osm.ui.pages.solution.SolutionScreen
import com.ih.osm.ui.utils.EMPTY
import com.ih.osm.ui.utils.LOAD_CATALOGS

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(700)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(700)) },
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(
            Screen.HomeV2.route,
            arguments = listOf(navArgument(ARG_SYNC_CATALOG) { type = NavType.StringType }),
        ) {
            val syncCatalogs = it.arguments?.getString(ARG_SYNC_CATALOG).orEmpty()
            HomeScreenV2(navController = navController, syncCatalogs = syncCatalogs)
        }
        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }
        composable(
            Screen.CardDetail.route,
            arguments = listOf(navArgument(ARG_CARD_ID) { type = NavType.StringType }),
        ) {
            val cardId = it.arguments?.getString(ARG_CARD_ID).orEmpty()
            CardDetailScreen(navController = navController, cardId = cardId)
        }
        composable(
            Screen.CreateCard.route,
            arguments = listOf(navArgument(ARG_CARD_FILTER) { type = NavType.StringType }),
        ) {
            val filter = it.arguments?.getString(ARG_CARD_FILTER).orEmpty()
            CreateCardScreen(navController = navController, filter = filter)
        }
        composable(Screen.Dev.route) {
            DevScreen()
        }
        composable(
            Screen.Solution.route,
            arguments =
                listOf(
                    navArgument(ARG_SOLUTION) { type = NavType.StringType },
                    navArgument(ARG_CARD_ID) { type = NavType.StringType },
                ),
        ) {
            val solutionType = it.arguments?.getString(ARG_SOLUTION).orEmpty()
            val cardId = it.arguments?.getString(ARG_CARD_ID).orEmpty()
            SolutionScreen(
                navController = navController,
                solutionType = solutionType,
                cardId = cardId,
            )
        }

        composable(
            Screen.CardList.route,
            arguments = listOf(navArgument(ARG_CARD_FILTER) { type = NavType.StringType }),
        ) {
            val filter = it.arguments?.getString(ARG_CARD_FILTER).orEmpty()
            CardListScreen(navController = navController, filter = filter)
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
    }
}

fun NavController.navigateAndClean(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}

fun NavController.navigateToHome() {
    navigateAndClean("${Screen.Home.path}?$ARG_SYNC_CATALOG=$LOAD_CATALOGS")
}

fun NavController.navigateToHomeV2() {
    navigateAndClean("${Screen.HomeV2.path}?$ARG_SYNC_CATALOG=$LOAD_CATALOGS")
}

fun NavController.navigateToLogin() {
    navigateAndClean(Screen.Login.route)
}

fun NavController.navigateToAccount() {
    navigate(Screen.Account.route)
}

fun NavController.navigateToCardDetail(id: String) {
    navigate(
        "${Screen.CardDetail.path}/$id",
    )
}

fun NavController.navigateToCardSolution(
    solutionType: String,
    cardId: String,
) {
    navigate(
        "${Screen.Solution.path}/$solutionType/$cardId",
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
