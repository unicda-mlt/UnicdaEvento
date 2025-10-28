package com.route.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.main.unicdaevento.screen.LoginScreen
import com.route.AppNestedRoute


fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AppNestedRoute.Main.route,
        startDestination = MainAppRoute.Login.route,
    ) {
        composable(
            MainAppRoute.Login.route
        ) {
            LoginScreen(navController)
        }
    }
}