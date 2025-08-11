package com.flow.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.database.AppDb
import com.flow.route.MainFlowRoute

@Composable
fun MyEventScreen(navController: NavHostController, db: AppDb) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(MainFlowRoute.MY_EVENT.title)
    }
}