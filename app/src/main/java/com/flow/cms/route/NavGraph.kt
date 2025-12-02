package com.flow.cms.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.presentation.route.AppNestedRoute
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flow.cms.scaffold.CMSDefaultFlowScaffold
import com.flow.cms.scaffold.CMSFlowScaffold
import com.flow.cms.screen.choose_location.ChooseLocationScreen
import com.flow.cms.screen.event.EventScreen
import com.flow.cms.screen.event_category.EventCategoryListScreen
import com.flow.cms.screen.event_department.EventDepartmentListScreen
import com.flow.cms.screen.event_form.EventFormScreen
import com.presentation.screen.event_form.EventFormBackEntryParam


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
        composable(CMSFlowRoute.CATEGORY_LIST.route) {
            CMSDefaultFlowScaffold(
                navController,
                CMSFlowRoute.CATEGORY_LIST.route
            ) {
                EventCategoryListScreen(navController)
            }
        }
        composable(CMSFlowRoute.EVENT_LIST.route) {
            CMSDefaultFlowScaffold(
                navController,
                CMSFlowRoute.EVENT_LIST.route
            ) {
                EventScreen(navController)
            }
        }
        composable(
            route = CMSFlowRoute.EVENT_FORM.route,
            arguments = listOf(navArgument("eventId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            CMSDefaultFlowScaffold(
                navController,
                CMSFlowRoute.EVENT_FORM.route
            ) {
                val eventId = backStackEntry.arguments?.getString("eventId")
                EventFormScreen(navController, eventId)
            }
        }
        composable(CMSFlowRoute.CHOOSE_LOCATION.route) { backStackEntry ->
            CMSDefaultFlowScaffold(
                navController,
                CMSFlowRoute.CHOOSE_LOCATION.route
            ) {
                ChooseLocationScreen(
                    onLocationSave = { latLng ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(EventFormBackEntryParam.Location.PARAM_NAME, latLng)

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}