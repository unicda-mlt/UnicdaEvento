package com.flow.student.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.route.AppNestedRoute
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.database.AppDb
import com.flow.student.screen.EventDetailScreen
import com.flow.student.scaffold.MainFlowScaffold


fun NavGraphBuilder.studentFlowNavGraph(
    navController: NavHostController,
    db: AppDb
) {
    navigation(
        route = AppNestedRoute.StudentFlow.route,
        startDestination = StudentFlowRoute.HOME.route
    ) {
        composable(StudentFlowRoute.HOME.route) {
            MainFlowScaffold(navController, db)
        }
        composable(
            route = StudentFlowRoute.EVENT_DETAIL.route,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId")
            EventDetailScreen(navController, db, eventId)
        }
    }
}