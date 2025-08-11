package com.route.main

import androidx.navigation.NavHostController

class AppRouter<T : MainAppRoute>(val screen: T, val navController: NavHostController)
