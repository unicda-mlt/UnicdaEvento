package com.route


sealed class AppNestedRoute(val route: String) {
    data object Main : AppNestedRoute("main")
    data object MainFlow : AppNestedRoute("main_flow")
    data object Lab20250724 : AppNestedRoute("lab_20250724")
}