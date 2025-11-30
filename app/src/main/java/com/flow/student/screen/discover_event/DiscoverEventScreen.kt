package com.flow.student.screen.discover_event

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.presentation.common.Option
import com.presentation.common.OptionPicker
import com.presentation.common.OptionPickerButton
import com.presentation.common.SearchInput
import com.flow.student.screen.discover_event.component.EventItem
import com.presentation.common.RangeDatePickerDialog
import com.flow.student.route.StudentFlowRoute
import com.presentation.theme.MyAppTheme

@Composable
fun DiscoverEventScreen(
    navController: NavHostController,
    vm: DiscoverEventScreenViewModel = hiltViewModel()
) {
    val params by vm.params.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        withFrameNanos {  }
        vm.retrieveDepartments()
        vm.retrieveCategories()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(
                top = 10.dp,
                bottom = 15.dp,
                start = 10.dp,
                end = 10.dp
            )
        ) {
            SearchInput(
                value = params.search,
                onValueChange = { vm.updateSearch(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val departments by vm.departments.collectAsStateWithLifecycle()
                val eventCategories by vm.eventCategories.collectAsStateWithLifecycle()

                val departmentOptions = remember(departments) {
                    listOf<Option<String?>>(Option(null, "All")) +
                            departments.map { item -> Option<String?>(item.id, item.name) }
                }

                val eventCategoryOptions = remember(eventCategories) {
                    listOf<Option<String?>>(Option(null, "All")) +
                            eventCategories.map { item -> Option<String?>(item.id, item.name) }
                }

                Box {
                    val showRangeCalendar = remember { mutableStateOf(false) }

                    RangeDatePickerButton(
                        showCalendar = showRangeCalendar,
                        initialStartUtcMillis = params.fromDate,
                        initialEndUtcMillis = params.toDate,
                        onDateSelected = vm::setRangeDate
                    )
                }

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Deparments",
                    options = departmentOptions,
                    selected = params.departmentId,
                    onSelect = vm::setDepartmentId
                )

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Categories",
                    options = eventCategoryOptions,
                    selected = params.categoryEventId,
                    onSelect = vm::setCategoryId
                )
            }
        }

        Box {
            val listState = rememberSaveable (saver = LazyListState.Saver) {
                LazyListState()
            }
            val events by vm.events.collectAsStateWithLifecycle(initialValue = emptyList())

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
                    key = { it.id },
                    contentType = { "event" }
                )
                { event ->
                    EventItem(
                        startDate = event.startDate,
                        endDate = event.endDate,
                        title = event.title,
                        principalImageUrl = event.principalImage,
                        onClick = { onEventClick(event.id) }
                    )
                }
            }
        }

    }
}

@Composable
private fun RangeDatePickerButton(
    showCalendar: MutableState<Boolean>,
    initialStartUtcMillis: Long? = null,
    initialEndUtcMillis: Long? = null,
    onDateSelected: (startDateTimeMillis: Long?, endDateTimeMillis: Long?) -> Unit,
) {
    val selected = remember { mutableStateOf(false) }

    OptionPickerButton(
        text = "Date",
        isSelected = selected.value,
        enabled = true,
        onClick = {
            showCalendar.value = true
        }
    )

    RangeDatePickerDialog(
        showState = showCalendar,
        initialStartUtcMillis = initialStartUtcMillis,
        initialEndUtcMillis = initialEndUtcMillis,
        onDateSelected = { startDateTimeMillis, endDateTimeMillis ->
            selected.value = (startDateTimeMillis != null || endDateTimeMillis != null)
            onDateSelected(startDateTimeMillis, endDateTimeMillis)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DiscoverEventScreen_Preview() {
    MyAppTheme {
        DiscoverEventScreen(navController = rememberNavController())
    }
}