package com.flow.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.component.CustomAsyncImage
import com.component.CustomCircularProgressIndicator
import com.component.GoogleMapBox
import com.component.PrimaryButton
import com.component.TopBarSimpleBack
import com.database.AppDb
import com.database.entities.Department
import com.database.entities.Event
import com.database.entities.EventCategory
import com.database.entities.EventWithRefs
import com.database.instanceAppDbInMemory
import com.domain.viewmodel.flow.main.EventDetailScreenViewModel
import com.example.unicdaevento.R
import com.flow.main.route.MainFlowRoute
import com.main.unicdaevento.MyAppTheme
import com.util.daoViewModelFactory
import com.util.formatEpochLongToFullMonthDayTimeYear


@Composable
fun EventDetailScreen(
    navController: NavHostController,
    db: AppDb,
    eventId: Long? = null,
) {
    val vm: EventDetailScreenViewModel = viewModel(
        factory = daoViewModelFactory(
            db = db,
            create = { EventDetailScreenViewModel(
                studentId = 1L,
                eventDao = it.eventDao(),
                eventStudentDao = it.eventStudentDao()
            ) }
        )
    )

    val ui by vm.uiState.collectAsStateWithLifecycle()
    val eventData by vm.event.collectAsStateWithLifecycle()
    val joinEvent = vm::joinStudent

    LaunchedEffect(eventId) {
        eventId?.let(vm::loadEvent)
    }

    Scaffold (
        topBar = {
            TopBarSimpleBack(navController, MainFlowRoute.EVENT_DETAIL.title)
        }
    ) { innerPadding ->
        ScreenContent(
            ui = ui,
            data = eventData,
            joinEvent = joinEvent,
            innerPadding = innerPadding
        )
    }
}

@Composable
private fun ScreenContent(
    ui: EventDetailScreenViewModel.UIState,
    data: EventWithRefs?,
    joinEvent: () -> Unit,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val event = data?.event

    if (ui.error != null) {
        Toast.makeText(context, "❌ ${ui.error}", Toast.LENGTH_SHORT).show()
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState()),
    ) {
        CustomAsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clipToBounds(),
            imageUrl = event?.principalImage
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(top = 0.dp, bottom = 15.dp, start = 15.dp, end = 15.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Section(
                title = event?.title,
                description = event?.description
            )

            SectionContainer {
                SectionTitleText("Date & Time")

                Column {
                    SectionDescriptionText(
                        if (event?.startDate != null)
                            formatEpochLongToFullMonthDayTimeYear(event.startDate)
                        else
                            ""
                    )

                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Box(
                            Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .align(Alignment.Top)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.arrow_down_to_left),
                                contentDescription = "Default image",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }


                        Column(
                            modifier = Modifier.height(50.dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            SectionDescriptionText(
                                if (event?.endDate != null)
                                    formatEpochLongToFullMonthDayTimeYear(event.endDate)
                                else
                                    ""
                            )

                        }
                    }
                }
            }

            Section(
                title = "Location",
                description = event?.location
            )

            if (event != null) {
                Map(
                    lat = event.latitude,
                    lng = event.longitude,
                )
            }

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !ui.loading && !ui.joining,
                onClick = { joinEvent() }
            ) {
                Box(
                    modifier = Modifier.height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (ui.loading || ui.joining) {
                        CustomCircularProgressIndicator()
                    } else {
                        Text("Join Event", fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    title: String?,
    description: String?
) {
    SectionContainer {
        SectionTitleText(title)
        SectionDescriptionText(description)
    }
}

@Composable
private fun SectionContainer(content: @Composable (() -> Unit) = {}) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        content()
    }
}

@Composable
private fun SectionTitleText(text: String?) {
    Text(
        text ?: "",
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight(500),
            color = Color.DarkGray
        )
    )
}

@Composable
private fun SectionDescriptionText(text: String?) {
    Text(
        text ?: "",
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            fontSize = 18.sp,
            color = Color.DarkGray
        )
    )
}

@Composable
private fun Map(
    lat: Double,
    lng: Double,
) {
    GoogleMapBox(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        lat = lat,
        lng = lng,
        zoom = 24f
    )
}

@Preview(showBackground = true)
@Composable
private fun EventDetailScreen_Preview() {
    val context = LocalContext.current
    val db = remember { instanceAppDbInMemory(context) }

    MyAppTheme {
        ScreenContent(
            joinEvent = {},
            innerPadding = PaddingValues(0.dp),
            ui = EventDetailScreenViewModel.UIState(
                loading = false,
                joining = false,
            ),
            data = EventWithRefs(
                Event(
                    id = 1L,
                    departmentId = 1L,
                    eventCategoryId = 1L,
                    title = "Advanced Android Development",
                    description = "Hands-on workshop covering Jetpack Compose, Room, and Hilt best practices.",
                    latitude = 18.4861,
                    longitude = -69.9312,
                    startDate = System.currentTimeMillis() + 86_400_000L,
                    endDate = System.currentTimeMillis() + (2 * 86_400_000L),
                    location = "Santo Domingo",
                    principalImage = "https://picsum.photos/seed/1/1280/720"
                ),
                Department(
                    id = 1L,
                    name = "Computer Science"
                ),
                EventCategory(
                    id = 1L,
                    name = "Workshop"
                )
            )
        )
    }
}