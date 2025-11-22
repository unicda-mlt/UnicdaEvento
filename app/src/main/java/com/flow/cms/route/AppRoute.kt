package com.flow.cms.route

import com.route.AppRouteInfo

sealed class CMSFlowRoute(override val route: String, override val title: String) : AppRouteInfo {
    data object HOME : CMSFlowRoute("cms_flow_home", "Home")

    data object DEPARTMENT_LIST : CMSFlowRoute("cms_flow_discover_event", "Departments")
    data object DEPARTMENT_FORM : CMSFlowRoute("cms_flow_discover_event", "Department Form")

    data object CATEGORY_LIST : CMSFlowRoute("cms_flow_discover_event", "Categories")
    data object CATEGORY_FORM : CMSFlowRoute("cms_flow_discover_event", "Category Form")

    data object EVENT_LIST : CMSFlowRoute("cms_flow_discover_event", "Events")
    data object EVENT_FORM : CMSFlowRoute("cms_flow_discover_event", "Event Form")

    data object SETTING : CMSFlowRoute("cms_flow_setting", "Setting")

    companion object {
        private val screens: List<CMSFlowRoute> by lazy {
            listOfNotNull(
                HOME,
                DEPARTMENT_LIST,
                DEPARTMENT_FORM,
                CATEGORY_LIST,
                CATEGORY_FORM,
                EVENT_LIST,
                EVENT_FORM,
                SETTING,
            )
        }

        fun fromRoute(route: String): AppRouteInfo? {
            return screens.singleOrNull {
                val base = it.route.substringBefore("/{")
                route.startsWith(base)
            }
        }
    }
}