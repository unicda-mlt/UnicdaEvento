package com.route


sealed class AppNestedRoute(val route: String) {
    data object Main : AppNestedRoute("main")
    data object StudentFlow : AppNestedRoute("student_flow")
}