package com.flow.cms.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.route.AppNestedRoute
import androidx.navigation.compose.composable
import com.flow.cms.scaffold.CMSFlowScaffold


fun NavGraphBuilder.cmsFlowNavGraph(
    navController: NavHostController
) {
    navigation(
        route = AppNestedRoute.CMSFlow.route,
        startDestination = CMSFlowRoute.HOME.route
    ) {
        composable(CMSFlowRoute.HOME.route) {
            CMSFlowScaffold(navController)
        }
    }
}