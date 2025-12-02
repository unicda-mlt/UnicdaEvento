package com.flow.cms.screen.event

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.domain.entities.Event
import com.flow.cms.route.CMSFlowRoute
import com.flow.cms.screen.event.component.EventItem
import com.presentation.common.Option
import com.presentation.common.OptionPicker
import com.presentation.common.OptionPickerButton
import com.presentation.common.SearchInput
import com.presentation.common.RangeDatePickerDialog
import com.presentation.theme.MyAppTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun EventScreen(
    navController: NavHostController,
    vm: EventScreenViewModel = hiltViewModel()
) {
    val onEventClick: (String) -> Unit = remember(navController) {
        { eventId ->
            navController.navigate(CMSFlowRoute.EVENT_FORM.create(eventId)) {
                launchSingleTop = true
            }
        }
    }

    val onAddEventClick: () -> Unit = remember(navController) {
        {
            navController.navigate(CMSFlowRoute.EVENT_FORM.create()) {
                launchSingleTop = true
            }
        }
    }
    
    LaunchedEffect(Unit) {
        withFrameNanos {  }
        vm.retrieveDepartments()
        vm.retrieveCategories()
    }

    Content(
        paramsFlow = vm.params,
        departmentsFlow = vm.departments,
        categoriesFlow = vm.eventCategories,
        eventsFlow = vm.events,
        updateSearch = vm::updateSearch,
        setRangeDate = vm::setRangeDate,
        setDepartmentId = vm::setDepartmentId,
        setCategoryId = vm::setCategoryId,
        onEventClick = onEventClick,
        onAddEventClick = onAddEventClick
    )
}

@Composable
private fun Content(
    paramsFlow: ParamFlow,
    departmentsFlow: DepartmentsFlow,
    categoriesFlow: CategoriesFlow,
    eventsFlow: EventsFlow,
    updateSearch: (value: String) -> Unit,
    setRangeDate: (fromDate: Long?, toDate: Long?) -> Unit,
    setDepartmentId: (id: String?) -> Unit,
    setCategoryId: (id: String?) -> Unit,
    onEventClick: (id: String) -> Unit,
    onAddEventClick: () -> Unit
) {
    val params by paramsFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(
                    top = 10.dp,
                    bottom = 15.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            SearchInput(
                value = params.search,
                onValueChange = { updateSearch(it) },
                modifier = Modifier.weight(1f)
            )

            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = onAddEventClick
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Box(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val departments by departmentsFlow.collectAsStateWithLifecycle()
                val eventCategories by categoriesFlow.collectAsStateWithLifecycle()

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
                        onDateSelected = setRangeDate
                    )
                }

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Deparments",
                    options = departmentOptions,
                    selected = params.departmentId,
                    onSelect = setDepartmentId
                )

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Categories",
                    options = eventCategoryOptions,
                    selected = params.categoryEventId,
                    onSelect = setCategoryId
                )
            }
        }

        Box {
            val listState = rememberSaveable (saver = LazyListState.Saver) {
                LazyListState()
            }
            val events by eventsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

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
private fun EventEventScreen_Preview() {
    val paramsFlow: ParamFlow = remember {
        MutableStateFlow(
            EventScreenViewModel.Params()
        )
    }

    val departmentsFlow: DepartmentsFlow = remember {
        MutableStateFlow(
            listOf()
        )
    }

    val categoriesFlow: CategoriesFlow = remember {
        MutableStateFlow(
            listOf()
        )
    }

    val eventsFlow: EventsFlow = remember {
        MutableStateFlow(
            listOf(
                Event(
                    id = "FO8toKr8RU0YF3llPGqc",
                    departmentId = "XkfdYTKaHANLifmg8FuB",
                    eventCategoryId = "AsGP0B3v9g5TW5l6TnEa",
                    title = "Intro to Kotlin",
                    description = "Basics of Kotlin and Compose for Android. Hosted by UNICDA.",
                    location = "Santo Domingo",
                    latitude = 18.4861,
                    longitude = -69.9312,
                    startDate = 1761606000000,
                    endDate = 1761613200000,
                    principalImage = "https://picsum.photos/seed/1/1280/720"
                )
            )
        )
    }

    MyAppTheme {
        Content(
            paramsFlow = paramsFlow,
            departmentsFlow = departmentsFlow,
            categoriesFlow = categoriesFlow,
            eventsFlow = eventsFlow,
            updateSearch = { _, -> },
            setRangeDate = { _, _, -> },
            setDepartmentId = { _, -> },
            setCategoryId = { _, -> },
            onEventClick = { _, -> },
            onAddEventClick = { }
        )
    }
}