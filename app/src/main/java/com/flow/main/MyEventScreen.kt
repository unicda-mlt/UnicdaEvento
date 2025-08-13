package com.flow.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.component.discover_event.EventItem
import com.database.AppDb
import com.domain.viewmodel.flow.main.MyEventScreenViewModel
import com.flow.main.route.MainFlowRoute
import com.util.daoViewModelFactory

@Composable
fun MyEventScreen(
    navController: NavHostController,
    db: AppDb
) {
    val vm: MyEventScreenViewModel = viewModel(
        factory = daoViewModelFactory(
            db = db,
            create = { MyEventScreenViewModel(
                studentDao = db.studentDao(),
                studentId = 1,
            ) }
        )
    )

    val events by vm.events.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(
                items = events,
                key = { it.id },
                contentType = { "event" }
            )
            { event ->
                val navigateToDetail = remember(event.id) {
                    {
                        navController.navigate(MainFlowRoute.EVENT_DETAIL.create(event.id)) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(MainFlowRoute.HOME.route) { saveState = true }
                        }
                    }
                }

                EventItem(
                    startDate = event.startDate,
                    endDate = event.endDate,
                    title = event.title,
                    principalImageUrl = event.principalImage,
                    onClick = navigateToDetail
                )
            }
        }
    }
}