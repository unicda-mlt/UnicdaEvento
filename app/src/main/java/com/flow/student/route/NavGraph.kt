package com.flow.student.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.presentation.route.AppNestedRoute
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flow.student.screen.event_detail.EventDetailScreen
import com.flow.student.scaffold.StudentFlowScaffold


fun NavGraphBuilder.studentFlowNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AppNestedRoute.StudentFlow.route,
        startDestination = StudentFlowRoute.HOME.route
    ) {
        composable(StudentFlowRoute.HOME.route) {
            StudentFlowScaffold(navController)
        }
        composable(
            route = StudentFlowRoute.EVENT_DETAIL.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            EventDetailScreen(navController, eventId)
        }
    }
}