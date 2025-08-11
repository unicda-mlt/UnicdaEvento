package com.flow.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.route.AppNestedRoute
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.database.AppDb
import com.flow.main.EventDetailScreen
import com.flow.scaffold.MainFlowScaffold


fun NavGraphBuilder.mainFlowNavGraph(
    navController: NavHostController,
    db: AppDb
) {
    navigation(
        route = AppNestedRoute.MainFlow.route,
        startDestination = MainFlowRoute.HOME.route
    ) {
        composable(MainFlowRoute.HOME.route) {
            MainFlowScaffold(navController, db)
        }
        composable(
            route = MainFlowRoute.EVENT_DETAIL.route,
            arguments = listOf(navArgument("eventId") { type = NavType.LongType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId")
            EventDetailScreen(navController, eventId, db)
        }
    }
}