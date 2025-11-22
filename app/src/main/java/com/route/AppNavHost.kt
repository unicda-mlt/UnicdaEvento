package com.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flow.cms.route.cmsFlowNavGraph
import com.flow.student.route.studentFlowNavGraph
import com.route.main.mainNavGraph


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController,
        startDestination = AppNestedRoute.Main.route
    ) {
        mainNavGraph(navController)
        studentFlowNavGraph(navController)
        cmsFlowNavGraph(navController)
    }
}