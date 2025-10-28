package com.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.animation.NavigationAnimation
import com.flow.student.route.studentFlowNavGraph
import com.route.main.mainNavGraph


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController,
        startDestination = AppNestedRoute.Main.route,
        enterTransition = { NavigationAnimation.SlideInHorizontalEnter },
        exitTransition = { NavigationAnimation.SlideInHorizontalExit },
        popEnterTransition = { NavigationAnimation.SlideInHorizontalPopEnter },
        popExitTransition = { NavigationAnimation.SlideInHorizontalPopExit }
    ) {
        mainNavGraph(navController)
        studentFlowNavGraph(navController)
    }
}