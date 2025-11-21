package com.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.presentation.animation.HorizontalNavigationAnimation
import com.flow.student.route.studentFlowNavGraph
import com.route.main.mainNavGraph


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController,
        startDestination = AppNestedRoute.Main.route,
        enterTransition = { HorizontalNavigationAnimation.SlideInHorizontalEnter },
        exitTransition = { HorizontalNavigationAnimation.SlideInHorizontalExit },
        popEnterTransition = { HorizontalNavigationAnimation.SlideInHorizontalPopEnter },
        popExitTransition = { HorizontalNavigationAnimation.SlideInHorizontalPopExit }
    ) {
        mainNavGraph(navController)
        studentFlowNavGraph(navController)
    }
}