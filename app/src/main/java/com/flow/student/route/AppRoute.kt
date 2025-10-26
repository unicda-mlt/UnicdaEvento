package com.flow.student.route

import com.route.AppRouteInfo

sealed class StudentFlowRoute(override val route: String, override val title: String) : AppRouteInfo {
    data object HOME : StudentFlowRoute("main_flow_home", "Home")
    data object DISCOVER_EVENT : StudentFlowRoute("main_flow_discover_event", "Discover")

    data object EVENT_DETAIL : StudentFlowRoute("main_flow_event_detail/{eventId}", "Event Details") {
        fun create(eventId: Long) = "main_flow_event_detail/$eventId"
    }

    data object MY_EVENT : StudentFlowRoute("main_flow_my_event", "My Events")
    data object SETTING : StudentFlowRoute("main_flow_setting", "Setting")

    companion object {
        private val screens: List<StudentFlowRoute> by lazy {
            listOfNotNull(
                HOME,
                DISCOVER_EVENT,
                EVENT_DETAIL,
                MY_EVENT,
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