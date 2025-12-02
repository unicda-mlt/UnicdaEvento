package com.flow.cms.route

import com.presentation.route.AppRouteInfo

sealed class CMSFlowRoute(override val route: String, override val title: String) : AppRouteInfo {
    data object HOME : CMSFlowRoute("cms_flow_home", "Home")

    data object DEPARTMENT_LIST : CMSFlowRoute("cms_flow_department_list", "Departments")

    data object CATEGORY_LIST : CMSFlowRoute("cms_flow_category_list", "Categories")

    data object EVENT_LIST : CMSFlowRoute("cms_flow_event_list", "Events")
    data object EVENT_FORM : CMSFlowRoute("cms_flow_event_form?eventId={eventId}", "Event Form") {
        fun create(eventId: String? = null): String {
            return if (eventId == null)
                "cms_flow_event_form"
            else
                "cms_flow_event_form?eventId=$eventId"
        }
    }

    data object SETTING : CMSFlowRoute("cms_flow_setting", "Setting")

    data object CHOOSE_LOCATION : CMSFlowRoute("cms_map_location_selection", "Choose location")

    companion object {
        private val screens: List<CMSFlowRoute> by lazy {
            listOfNotNull(
                HOME,
                DEPARTMENT_LIST,
                CATEGORY_LIST,
                EVENT_LIST,
                EVENT_FORM,
                SETTING,
                CHOOSE_LOCATION,
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