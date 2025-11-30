package com.flow.main.route

import com.presentation.route.AppRouteInfo


sealed class MainAppRoute(override val route: String, override val title: String) : AppRouteInfo {
    data object Login : MainAppRoute("login", "Login")

    companion object {
        private val screens: List<MainAppRoute> by lazy {
            listOfNotNull(
                Login
            )
        }

        fun fromRoute(route: String): AppRouteInfo? {
            return screens.singleOrNull { it.route == route }
        }
    }
}