package com.flow.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.component.SearchInput
import com.component.discover_event.EventItem
import com.database.AppDb
import com.database.instanceAppDbInMemory
import com.domain.flow.main.DiscoverEventScreenViewModel
import com.flow.route.MainFlowRoute
import com.main.unicdaevento.MyAppTheme

@Composable
fun DiscoverEventScreen(
    navController: NavHostController,
    db: AppDb,
) {
    val vm: DiscoverEventScreenViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DiscoverEventScreenViewModel(db.eventDao()) as T
            }
        }
    )

    val ui by vm.ui.collectAsStateWithLifecycle()
    val events by vm.events.collectAsStateWithLifecycle(initialValue = emptyList())

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

        LazyColumn {
            items(events, key = { it.id }) { event ->
                EventItem(
                    startDate = event.startDate,
                    endDate = event.endDate,
                    title = event.title,
                    onClick = {
                        navController.navigate(MainFlowRoute.EVENT_DETAIL.create(event.id))
                    }
                )
            }
        }
    }
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