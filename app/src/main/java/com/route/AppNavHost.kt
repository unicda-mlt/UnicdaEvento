package com.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.animation.NavigationAnimation
import com.database.AppDb
import com.flow.student.route.studentFlowNavGraph
import com.route.main.mainNavGraph


@Composable
fun AppNavHost(
    navController: NavHostController,
    db: AppDb
) {
    NavHost(
        navController,
        startDestination = AppNestedRoute.Main.route,
        enterTransition = { NavigationAnimation.SlideInHorizontalEnter },
        exitTransition = { NavigationAnimation.SlideInHorizontalExit },
        popEnterTransition = { NavigationAnimation.SlideInHorizontalPopEnter },
        popExitTransition = { NavigationAnimation.SlideInHorizontalPopExit }
    ) {
        mainNavGraph(navController, db)
        studentFlowNavGraph(navController, db)
    }
}