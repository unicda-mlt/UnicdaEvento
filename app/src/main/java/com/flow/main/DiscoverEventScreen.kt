package com.flow.main

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.component.Option
import com.component.OptionPicker
import com.component.OptionPickerButton
import com.component.SearchInput
import com.component.discover_event.EventItem
import com.component.RangeDatePickerDialog
import com.database.AppDb
import com.database.instanceAppDbInMemory
import com.domain.viewmodel.flow.main.DiscoverEventScreenViewModel
import com.flow.main.route.MainFlowRoute
import com.main.unicdaevento.MyAppTheme
import com.util.daoViewModelFactory

@Composable
fun DiscoverEventScreen(
    navController: NavHostController,
    db: AppDb,
) {
    val vm: DiscoverEventScreenViewModel = viewModel(
        factory = daoViewModelFactory(
            db = db,
            create = { DiscoverEventScreenViewModel(
                eventDao = it.eventDao(),
                departmentDao = it.departmentDao(),
                eventCategoryDao = it.eventCategoryDao()
            ) }
        )
    )

    val ui by vm.ui.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle(initialValue = emptyList())
    val departments by vm.departments.collectAsStateWithLifecycle()
    val eventCategories by vm.eventCategories.collectAsStateWithLifecycle()
    val showRangeCalendar = remember { mutableStateOf(false) }

    val departmentOptions = remember(departments) {
        listOf<Option<Long?>>(Option(null, "All")) +
                departments.map { item -> Option<Long?>(item.id, item.name) }
    }

    val eventCategoryOptions = remember(eventCategories) {
        listOf<Option<Long?>>(Option(null, "All")) +
                eventCategories.map { item -> Option<Long?>(item.id, item.name) }
    }

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
                value = ui.search,
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
                RangeDatePickerButton(
                    showCalendar = showRangeCalendar,
                    initialStartUtcMillis = ui.fromDate,
                    initialEndUtcMillis = ui.toDate,
                    onDateSelected = vm::setRangeDate
                )

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Deparments",
                    options = departmentOptions,
                    selected = ui.departmentId,
                    onSelect = vm::setDepartmentId
                )

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Categories",
                    options = eventCategoryOptions,
                    selected = ui.categoryEventId,
                    onSelect = vm::setCategoryId
                )
            }
        }

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
    val context = LocalContext.current
    val db = remember { instanceAppDbInMemory(context) }

    MyAppTheme {
        DiscoverEventScreen(navController = rememberNavController(), db)
    }
}