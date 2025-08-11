package com.flow.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.component.TopBarSimpleBack
import com.database.AppDb
import com.flow.route.MainFlowRoute

@Composable
fun EventDetailScreen(
    navController: NavHostController,
    eventId: Long?,
    db: AppDb
) {
    Scaffold (
        topBar = {
            TopBarSimpleBack(navController, MainFlowRoute.EVENT_DETAIL.title)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Text(MainFlowRoute.EVENT_DETAIL.title)
        }
    }
}