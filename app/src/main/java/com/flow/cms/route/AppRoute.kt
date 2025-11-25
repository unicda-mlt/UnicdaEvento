package com.flow.cms.route

import com.route.AppRouteInfo

sealed class CMSFlowRoute(override val route: String, override val title: String) : AppRouteInfo {
    data object HOME : CMSFlowRoute("cms_flow_home", "Home")

    data object DEPARTMENT_LIST : CMSFlowRoute("cms_flow_department_list", "Departments")
    data object DEPARTMENT_FORM : CMSFlowRoute("cms_flow_department_form", "Department Form")

    data object CATEGORY_LIST : CMSFlowRoute("cms_flow_category_list", "Categories")
    data object CATEGORY_FORM : CMSFlowRoute("cms_flow_category_form", "Category Form")

    data object EVENT_LIST : CMSFlowRoute("cms_flow_event_list", "Events")
    data object EVENT_FORM : CMSFlowRoute("cms_flow_event_form", "Event Form")

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