package com.flow.cms.screen.event_form

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.domain.entities.Department
import com.domain.entities.EventCategory
import com.flow.cms.route.CMSFlowRoute
import com.google.android.gms.maps.model.LatLng
import com.presentation.common.CustomAsyncImage
import com.presentation.common.DateTimePicker
import com.presentation.common.GoogleMapBox
import com.presentation.common.LoadingOverlay
import com.presentation.common.Option
import com.presentation.common.OptionPicker
import com.presentation.screen.event_form.EventFormBackEntryParam
import com.presentation.theme.MyAppTheme
import com.util.formatEpochLongToFullYearMonthDayTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.String


@Composable
fun EventFormScreen(
    navController: NavHostController,
    eventId: String? = null,
    vm: EventFormScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()
    val ui by vm.uiState.collectAsStateWithLifecycle()
    val formHasSaved by vm.formHasSaved.collectAsStateWithLifecycle()

    val chosenLocation by remember(backStackEntry) {
        backStackEntry
            ?.savedStateHandle
            ?.getStateFlow<EventFormBackEntryParam.Location?>(
                EventFormBackEntryParam.Location.PARAM_NAME,
                null
            )
            ?: MutableStateFlow(null)
    }.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.retrieveDepartments()
        vm.retrieveCategories()
    }

    LaunchedEffect(eventId) {
        eventId?.let(vm::loadEvent)
    }

    LaunchedEffect(chosenLocation) {
        val location = chosenLocation ?: return@LaunchedEffect

        delay(300)
        vm.updateLocation(location.locationDisplayName)
        vm.updateLatLng(location.latLng)
        backStackEntry?.savedStateHandle?.set(
            EventFormBackEntryParam.Location.PARAM_NAME,
            null
        )
    }

    LaunchedEffect(formHasSaved) {
        if (formHasSaved) {
            navController.popBackStack()
        }
    }

    if (ui.error != null) {
        Toast.makeText(context, "❌ ${ui.error}", Toast.LENGTH_SHORT).show()
    }

    LoadingOverlay(ui.loading)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ScreenContent(
            onGetLocationClick = {
                navController.navigate(CMSFlowRoute.CHOOSE_LOCATION.route) {
                    launchSingleTop = true
                }
            },
            departmentsFlow = vm.departments,
            categoriesFlow = vm.categories,
            departmentIdFlow = vm.departmentId,
            categoryIdFlow = vm.categoryId,
            titleFlow = vm.title,
            descriptionFlow = vm.description,
            locationFlow = vm.location,
            latLngFlow = vm.latLng,
            startDateFlow = vm.startDate,
            endDateFlow = vm.endDate,
            principalImageFlow = vm.principalImage,
            updateDepartmentId = vm::updateDepartmentId,
            updateCategoryId = vm::updateCategoryId,
            updateTitle = vm::updateTitle,
            updateDescription = vm::updateDescription,
            updateLocation = vm::updateLocation,
            updateStartDate = vm::updateStartDate,
            updateEndDate = vm::updateEndDate,
            updatePrincipalImage = vm::updatePrincipalImage,
        )

        FloatingActionButton(
            onClick = { vm.save() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save"
            )
        }
    }
}

@Composable
private fun ScreenContent(
    onGetLocationClick: () -> Unit,
    departmentsFlow: StateFlow<List<Department>>,
    categoriesFlow: StateFlow<List<EventCategory>>,
    departmentIdFlow: StateFlow<String?>,
    categoryIdFlow: StateFlow<String?>,
    titleFlow: StateFlow<String?>,
    descriptionFlow: StateFlow<String?>,
    locationFlow: StateFlow<String?>,
    latLngFlow: StateFlow<LatLng?>,
    startDateFlow: StateFlow<Long?>,
    endDateFlow: StateFlow<Long?>,
    principalImageFlow: StateFlow<String?>,
    updateDepartmentId: (id: String?) -> Unit,
    updateCategoryId: (id: String?) -> Unit,
    updateTitle: (title: String?) -> Unit,
    updateDescription: (description: String?) -> Unit,
    updateLocation: (location: String?) -> Unit,
    updateStartDate: (date: Long?) -> Unit,
    updateEndDate: (date: Long?) -> Unit,
    updatePrincipalImage: (url: String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val departments by departmentsFlow.collectAsStateWithLifecycle()
            val eventCategories by categoriesFlow.collectAsStateWithLifecycle()

            val departmentOptions = remember(departments) {
                departments.map { item -> Option<String?>(item.id, item.name) }
            }

            val eventCategoryOptions = remember(eventCategories) {
                eventCategories.map { item -> Option(item.id, item.name) }
            }

            Box {
                val departmentId by departmentIdFlow.collectAsStateWithLifecycle()

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Deparments",
                    options = departmentOptions,
                    selected = departmentId,
                    onSelect = updateDepartmentId
                )
            }

            Box {
                val categoryId by categoryIdFlow.collectAsStateWithLifecycle()

                OptionPicker(
                    modifier = Modifier.width(150.dp),
                    title = "Categories",
                    options = eventCategoryOptions,
                    selected = categoryId,
                    onSelect = updateCategoryId
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val principalImage by principalImageFlow.collectAsStateWithLifecycle()

            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var url by rememberSaveable(principalImage) { mutableStateOf(principalImage ?: "") }

                TextField(
                    modifier = Modifier.height(60.dp).weight(1f),
                    value = url,
                    onValueChange = { it -> url = it },
                    textStyle = ScreenTextStyle.description,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(
                            text = "Image URL",
                            style = ScreenTextStyle.description
                        )
                    },
                    suffix = {
                        if (!url.isEmpty()) {
                            Column(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .combinedClickable(
                                        onClick = {
                                            url = principalImage ?: ""
                                        },
                                        onLongClick = {
                                            url = ""
                                        },
                                        role = Role.Button
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                )

                Button(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { updatePrincipalImage(url) }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Add",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            CustomAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clipToBounds(),
                horizontalAlignment = Alignment.CenterHorizontally,
                imageUrl = principalImage,
                width = 1280.dp,
                height = 720.dp
            )
        }

        Spacer(Modifier.height(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            SectionContainer {
                Box {
                    val title by titleFlow.map { it ?: "" }
                        .collectAsStateWithLifecycle("")

                    PlainTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = { it -> updateTitle(it) },
                        textStyle = ScreenTextStyle.title,
                        placeholderText = "Title or Name"
                    )
                }

                Box {
                    val description by descriptionFlow.map { it ?: "" }
                        .collectAsStateWithLifecycle("")

                    PlainTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = description,
                        onValueChange = { it -> updateDescription(it) },
                        textStyle = ScreenTextStyle.description,
                        placeholderText = "Description"
                    )
                }
            }

            SectionContainer {
                SectionTitleText(
                    text = "Date & Time"
                )

                Column (
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DateSelection(
                        dateFlow = startDateFlow,
                        updateDate = updateStartDate,
                        textWhenNull = "Start date",
                        icon = Icons.Filled.PlayArrow,
                    )

                    DateSelection(
                        dateFlow = endDateFlow,
                        updateDate = updateEndDate,
                        textWhenNull = "End date",
                        icon = Icons.Filled.Stop
                    )
                }
            }

            SectionContainer {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitleText(
                        modifier = Modifier.weight(1f),
                        text = "Location"
                    )

                    Button(
                        modifier = Modifier.width(40.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        onClick = onGetLocationClick
                    ) {
                        Icon(
                            Icons.Filled.AddLocationAlt,
                            contentDescription = "Add",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Box {
                    val location by locationFlow.map { it ?: "" }
                        .collectAsStateWithLifecycle("")

                    PlainTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = location,
                        onValueChange = { it -> updateLocation(it) },
                        textStyle = ScreenTextStyle.description,
                        placeholderText = "Location address"
                    )
                }

                Box {
                    val latLng by latLngFlow.collectAsStateWithLifecycle()

                    Map(
                        latLng = latLng
                    )
                }
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}

object ScreenTextStyle {
    val title = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight(500),
        color = Color.DarkGray
    )
    val description = TextStyle(
        fontSize = 18.sp,
        color = Color.DarkGray
    )
    val date = TextStyle(
        color = Color.DarkGray,
        fontWeight = FontWeight(400),
        fontSize = 20.sp
    )
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
private fun SectionTitleText(
    modifier: Modifier = Modifier,
    text: String?
) {
    Text(
        text ?: "",
        modifier = modifier,
        style = ScreenTextStyle.title
    )
}

@Composable
private fun PlainTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (value: String) -> Unit,
    textStyle: TextStyle,
    placeholderText: String? = null
) {
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { it -> onValueChange(it) },
        textStyle = textStyle,
        placeholder = {
            if (placeholderText != null) {
                Text(
                    text = placeholderText,
                    style = textStyle.copy(color = textStyle.color.copy(alpha = 0.5f))
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
private fun DateSelection(
    dateFlow: StateFlow<Long?>,
    updateDate: (Long?) -> Unit,
    textWhenNull: String,
    icon: ImageVector
) {
    val date by dateFlow.collectAsStateWithLifecycle()
    val showDateSelector = remember { mutableStateOf(false) }

    DateTimePicker(
        value = date ?: System.currentTimeMillis(),
        onValueChange = updateDate,
        showState = showDateSelector
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.toString(),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(4.dp))

        Text(
            if (date != null)
                formatEpochLongToFullYearMonthDayTime(date!!)
            else textWhenNull,
            style = ScreenTextStyle.date
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { showDateSelector.value = true }
                .padding(4.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(26.dp),
                imageVector = Icons.Filled.Event,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun Map(
    latLng: LatLng?
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var show by remember { mutableStateOf(false) }
    val mapHeight = 300.dp

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                show = true
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (latLng == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .background(Color.Gray.copy(0.5f))
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Location not selected",
                fontSize = 24.sp,
                fontWeight = FontWeight(600),
                color = Color.Black.copy(0.7f)
            )
        }
    } else if (show) {
        GoogleMapBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight),
            lat = latLng.latitude,
            lng = latLng.longitude,
            zoom = 20f
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailScreen_Preview() {
    MyAppTheme {
        ScreenContent(
            onGetLocationClick = { },
            departmentsFlow = MutableStateFlow(listOf()),
            categoriesFlow = MutableStateFlow(listOf()),
            departmentIdFlow = MutableStateFlow(""),
            categoryIdFlow = MutableStateFlow(""),
            titleFlow = MutableStateFlow(""),
            descriptionFlow = MutableStateFlow("Hands-on workshop covering Jetpack Compose, Room, and Hilt best practices."),
            locationFlow = MutableStateFlow("Santo Domingo"),
            latLngFlow = MutableStateFlow(null),
            startDateFlow = MutableStateFlow(System.currentTimeMillis() + 86_400_000L),
            endDateFlow = MutableStateFlow(System.currentTimeMillis() + (2 * 86_400_000L)),
            principalImageFlow = MutableStateFlow("https://picsum.photos/seed/1/1280/720"),
            updateDepartmentId = { _ -> },
            updateCategoryId = { _ -> },
            updateTitle = { _ -> },
            updateDescription = { _ -> },
            updateLocation = { _ -> },
            updateStartDate = { _ -> },
            updateEndDate = { _ -> },
            updatePrincipalImage = { _ -> }
        )
    }
}