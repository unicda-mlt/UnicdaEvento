package com.route.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.database.AppDb
import com.main.unicdaevento.LoginScreen
import com.route.AppNestedRoute


fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    db: AppDb
) {
    navigation(
        route = AppNestedRoute.Main.route,
        startDestination = MainAppRoute.Login.route,
    ) {
        composable(
            MainAppRoute.Login.route
        ) {
            LoginScreen(navController, studentDao = db.studentDao())
        }
    }
}