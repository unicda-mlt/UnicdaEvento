package com.flow.student.screen.my_event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.presentation.common.CenterPopupMenuDialog
import com.presentation.common.MenuOption
import com.presentation.screen.event.EventItem
import com.flow.student.route.StudentFlowRoute

@Composable
fun MyEventScreen(
    navController: NavHostController,
    vm: MyEventScreenViewModel = hiltViewModel()
) {
    val events by vm.events.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val listState = rememberSaveable (saver = LazyListState.Saver) {
            LazyListState()
        }

        val onEventClick: (String) -> Unit = remember(navController) {
            { eventId ->
                navController.navigate(StudentFlowRoute.EVENT_DETAIL.create(eventId)) {
                    launchSingleTop = true
                }
            }
        }

        LazyColumn (
            state = listState
        ) {
            items(
                items = events,
                key = { it.event.id },
                contentType = { "event" }
            )
            { userEvent ->
                val event = userEvent.event

                EventItem(
                    startDate = event.startDate,
                    endDate = event.endDate,
                    title = event.title,
                    principalImageUrl = event.principalImage,
                    onClick = { onEventClick(event.id) },
                    onLongClick = {
                        vm.setSelectedEventId(userEvent.id)
                    }
                )
            }
        }
    }

    Box {
        val selectedEventId by vm.selectedEventId.collectAsStateWithLifecycle()

        val opts = listOf(
            MenuOption(value = selectedEventId, label = "Unjoin", isDestructive = true)
        )

        CenterPopupMenuDialog(
            visible = selectedEventId != null,
            options = opts,
            title = "Options",
            onDismiss = { vm.setSelectedEventId(null) },
            onSuccess = { value ->
                if (value != null) {
                    vm.unjoinEvent(value)
                }
            }
        )
    }
}