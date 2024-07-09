package com.ih.m2.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ih.m2.ui.pages.account.AccountScreen
import com.ih.m2.ui.pages.carddetail.CardDetailScreen
import com.ih.m2.ui.pages.createcard.CreateCardScreen
import com.ih.m2.ui.pages.dev.DevScreen
import com.ih.m2.ui.pages.home.HomeScreen
import com.ih.m2.ui.pages.home.PreHomeScreen
import com.ih.m2.ui.pages.login.LoginScreen
import com.ih.m2.ui.pages.solution.SolutionScreen
import com.ih.m2.ui.utils.LOAD_CATALOGS

@Composable
fun AppNavigation(
    startDestination: String,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(
            Screen.Home.route,
            arguments = listOf(navArgument(ARG_SYNC_CATALOG) { type = NavType.StringType })
        ) {
            val syncCatalogs = it.arguments?.getString(ARG_SYNC_CATALOG).orEmpty()
            HomeScreen(navController = navController, syncCatalogs = syncCatalogs)
        }
        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }
        composable(
            Screen.CardDetail.route,
            arguments = listOf(navArgument(ARG_CARD_ID) { type = NavType.StringType })
        ) {
            val cardId = it.arguments?.getString(ARG_CARD_ID).orEmpty()
            CardDetailScreen(navController = navController, cardId = cardId)
        }
        composable(Screen.CreateCard.route) {
            CreateCardScreen(navController = navController)
        }
        composable(Screen.Dev.route) {
            DevScreen()
        }
        composable(
            Screen.Solution.route,
            arguments = listOf(
                navArgument(ARG_SOLUTION) { type = NavType.StringType },
                navArgument(ARG_CARD_ID) { type = NavType.StringType })
        ) {
            val solutionType = it.arguments?.getString(ARG_SOLUTION).orEmpty()
            val cardId = it.arguments?.getString(ARG_CARD_ID).orEmpty()
            SolutionScreen(navController = navController, solutionType = solutionType, cardId = cardId)
        }

        composable(Screen.PreHome.route) {
            PreHomeScreen(navController = navController)
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
fun NavController.navigateToHome(param: String) {
    navigate("${Screen.Home.path}?$ARG_SYNC_CATALOG=")
}

fun NavController.navigateToLogin() {
    navigateAndClean(Screen.Login.route)
}

fun NavController.navigateToAccount() {
    navigate(Screen.Account.route)
}

fun NavController.navigateToCardDetail(id: String) {
    navigate(
        "${Screen.CardDetail.path}/$id"
    )
}

fun NavController.navigateToCardSolution(solutionType: String, cardId: String) {
    navigate(
        "${Screen.Solution.path}/$solutionType/$cardId"
    )
}

fun NavController.navigateToCreateCard() {
    navigate(Screen.CreateCard.route)
}