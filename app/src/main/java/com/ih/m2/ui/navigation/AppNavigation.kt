package com.ih.m2.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ih.m2.ui.pages.account.AccountScreen
import com.ih.m2.ui.pages.carddetail.CardDetailScreen
import com.ih.m2.ui.pages.createcard.CreateCardScreen
import com.ih.m2.ui.pages.home.HomeScreen
import com.ih.m2.ui.pages.login.LoginScreen

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
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }
        composable(Screen.CardDetail.route) {
            val cardId = it.arguments?.getString(ARG_CARD_ID).orEmpty()
            CardDetailScreen(navController = navController, cardId = cardId)
        }
        composable(Screen.CreateCard.route) {
            CreateCardScreen(navController = navController)
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
    navigateAndClean(Screen.Home.route)
}

fun NavController.navigateToLogin() {
    navigateAndClean(Screen.Login.route)
}

fun NavController.navigateToAccount() {
    navigate(Screen.Account.route)
}

fun NavController.navigateToCardDetail(id: String) {
    navigate(
        Screen.CardDetail.route
            .replace(
                oldValue = "{${ARG_CARD_ID}}",
                newValue = id
            )
    )
}

fun NavController.navigateToCreateCard() {
    navigate(Screen.CreateCard.route)
}