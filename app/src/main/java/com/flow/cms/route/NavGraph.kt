package com.flow.cms.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.route.AppNestedRoute
import androidx.navigation.compose.composable
import com.flow.cms.scaffold.CMSDefaultFlowScaffold
import com.flow.cms.scaffold.CMSFlowScaffold
import com.flow.cms.screen.event_department.EventDepartmentListScreen


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
        composable(CMSFlowRoute.DEPARTMENT_LIST.route) {
            CMSDefaultFlowScaffold(
                navController,
                CMSFlowRoute.DEPARTMENT_LIST.route
            ) {
                EventDepartmentListScreen(navController)
            }
        }
    }
}