package com.flow.cms.screen.choose_location

import android.location.Address
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.presentation.common.SearchInput
import com.presentation.screen.event_form.EventFormBackEntryParam
import com.presentation.theme.MyAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale


private data class MarkerPosition(
    val latLng: LatLng,
    val poiName: String? = null,
    val zoom: Float? = null,
)

@Composable
fun ChooseLocationScreen(
    vm: ChooseLocationScreenViewModel = hiltViewModel(),
    onLocationSave: (EventFormBackEntryParam.Location) -> Unit
) {
    var selectedPoi by remember { mutableStateOf<PointOfInterest?>(null) }
    var selectedPoiItem by remember { mutableStateOf<ChooseLocationScreenViewModel.PoiItem?>(null) }
    var markerPosition by remember { mutableStateOf<MarkerPosition?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(context) {
        vm.setContext(context)
    }

    LaunchedEffect(selectedPoi, markerPosition) {
        val latLng = selectedPoi?.latLng ?: markerPosition?.latLng ?: return@LaunchedEffect
        val poiName = selectedPoi?.name ?: markerPosition?.poiName ?: return@LaunchedEffect

        vm.updateLatestLatLng(EventFormBackEntryParam.Location(
            latLng = latLng,
            locationDisplayName = poiName
        ))

        vm.getMarkerAddressDetails(latLng, poiName)
    }

    LaunchedEffect(selectedPoi) {
        val poi = selectedPoi ?: return@LaunchedEffect

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                poi.latLng,
                cameraPositionState.position.zoom
            )
        )
    }

    LaunchedEffect(markerPosition) {
        if (markerPosition == null) {
            return@LaunchedEffect
        }

        val latLng = markerPosition!!.latLng
        val zoom = markerPosition!!.zoom

        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                latLng,
                zoom ?: cameraPositionState.position.zoom
            )
        )
    }

    LaunchedEffect(selectedPoiItem) {
        scope.launch {
            if (selectedPoiItem == null) {
                return@launch
            }

            vm.updateSearch(null)
            keyboardController?.hide()
            focusManager.clearFocus()

            delay(300)

            vm.getPoiLatLng(
                placeId = selectedPoiItem!!.placeId,
                onResult = { latLng, poiName ->
                    if (latLng != null) {
                        markerPosition = MarkerPosition(latLng, poiName, 17f)
                    }
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                selectedPoi = null
                markerPosition = MarkerPosition(latLng)
            },
            onPOIClick = { poi ->
                selectedPoi = poi
                markerPosition = MarkerPosition(poi.latLng)
            }
        ) {
            markerPosition?.let { pos ->
                Marker(
                    state = rememberUpdatedMarkerState(pos.latLng),
                    title = "Selected location"
                )
            }
        }

        Column {
            SearchableLocationByName(
                searchFlow = vm.search,
                poiListFlow = vm.poiList,
                updateSearch = vm::updateSearch,
                onItemClick = { it ->
                    selectedPoiItem = it
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            ZoomSectionButton(
                cameraPositionState = cameraPositionState
            )

            SectionLocationSearched(
                responseStateFlow = vm.markerAddressDetail,
                onLocationSaveClick = {
                    val location = vm.getLatestLocation()
                    if (location != null) {
                        onLocationSave(location)
                    }
                }
            )
        }
    }
}

@Composable
private fun ZoomSectionButton(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState
) {
    val scope = rememberCoroutineScope()

    Row {
        Spacer(Modifier.weight(1f))

        Column(
            modifier = modifier
                .padding(12.dp)
        ) {
            ZoomButton(Icons.Filled.Add) {
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.zoomIn(),
                        durationMs = 250
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ZoomButton(Icons.Filled.Remove) {
                scope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.zoomOut(),
                        durationMs = 250
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoomButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(40.dp)
            .clip(shape = RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = "Add",
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SectionLocationSearched(
    responseStateFlow: StateFlow<ChooseLocationScreenViewModel.ResponseState<ChooseLocationScreenViewModel.GotAddress>>,
    onLocationSaveClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        val responseState by responseStateFlow.collectAsStateWithLifecycle()

        when(responseState) {

            is ChooseLocationScreenViewModel.ResponseState.Idle -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Search a place",
                    color = Color.DarkGray,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(600)
                )
            }
            is ChooseLocationScreenViewModel.ResponseState.Error -> {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Address not found",
                    color = Color.DarkGray,
                    fontSize = 24.sp,
                    fontWeight = FontWeight(600)
                )
            }
            is ChooseLocationScreenViewModel.ResponseState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
                ) {
                    Text(
                        text = "Searching address…",
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        fontSize = 24.sp
                    )

                    CircularProgressIndicator(
                        strokeWidth = 2.dp
                    )
                }
            }
            is ChooseLocationScreenViewModel.ResponseState.Success -> {
                Row (
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                top = 15.dp,
                                start = 15.dp,
                                bottom = 5.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val (address, poiName) = (responseState as ChooseLocationScreenViewModel.ResponseState.Success<ChooseLocationScreenViewModel.GotAddress>).data

                        Text(
                            text = poiName ?: address.featureName,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight(600),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        val street = listOfNotNull(
                            address.thoroughfare?.takeIf { it.isNotBlank() },
                            address.subThoroughfare?.takeIf { it.isNotBlank() }
                        ).joinToString(", ")

                        Text(
                            text = street,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(400),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${address.locality}, ${address.countryName}",
                            color = Color.DarkGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight(400),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(60.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(
                            topEnd = 24.dp
                        ),
                        onClick = onLocationSaveClick
                    ) {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Add",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun SearchableLocationByName(
    searchFlow: StateFlow<String?>,
    poiListFlow: StateFlow<List<ChooseLocationScreenViewModel.PoiItem>>,
    updateSearch: (value: String?) -> Unit,
    onItemClick: (poi: ChooseLocationScreenViewModel.PoiItem) -> Unit
) {
    val search by searchFlow.collectAsStateWithLifecycle()
    val poiList by poiListFlow.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        SearchInput(
            modifier = Modifier.fillMaxWidth(),
            value = search ?: "",
            onValueChange = { it -> updateSearch(it) },
            rounded = false,
            placeholder = {
                Text(
                    text = "Search here",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        )

        AnimatedVisibility(
            visible = poiList.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(1f)
                    .background(Color.White)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = poiList,
                        key = { it.placeId }
                    ) { item ->
                        LocationResultItem(
                            poi = item,
                            onClick = onItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationResultItem(
    poi: ChooseLocationScreenViewModel.PoiItem,
    onClick: (poi: ChooseLocationScreenViewModel.PoiItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick(poi) }
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = poi.name,
            fontSize = 16.sp,
            maxLines = 1
        )

        Text(
            text = poi.address,
            fontSize = 14.sp,
            color = Color.DarkGray,
            maxLines = 1
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = DividerDefaults.color.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SectionLocationSearched_Preview() {
    MyAppTheme {
        Column {
            SearchableLocationByName(
                searchFlow = MutableStateFlow(""),
                poiListFlow = MutableStateFlow(listOf()),
                updateSearch = { _ -> },
                onItemClick = { _ -> }
            )

            Spacer(Modifier.weight(1f))

            ZoomSectionButton(cameraPositionState = rememberCameraPositionState())

//            SectionLocationSearched(
//                responseStateFlow = MutableStateFlow(ChooseLocationScreenViewModel.ResponseState.Idle)
//            )
//            SectionLocationSearched(
//                responseStateFlow = MutableStateFlow(ChooseLocationScreenViewModel.ResponseState.Error(Throwable("")))
//            )
//            SectionLocationSearched(
//                responseStateFlow = MutableStateFlow(ChooseLocationScreenViewModel.ResponseState.Loading)
//            )
            SectionLocationSearched(
                responseStateFlow = MutableStateFlow(ChooseLocationScreenViewModel.ResponseState.Success(
                    ChooseLocationScreenViewModel.GotAddress(zonaColonialAddress())
                )),
                onLocationSaveClick = { -> }
            )
        }
    }
}

private fun zonaColonialAddress(): Address {
    return Address(Locale.getDefault()).apply {
        // Feature / Place Name
        featureName = "Zona Colonial"

        // Street (thoroughfare) — most central reference
        thoroughfare = "Calle El Conde"

        // Optional house number (not known)
        subThoroughfare = null

        // Neighborhood
        subLocality = "Zona Colonial"

        // City
        locality = "Santo Domingo"

        // Province
        adminArea = "Distrito Nacional"

        // Postal code (common for Colonial Zone)
        postalCode = "10210"

        // Country info
        countryName = "Dominican Republic"
        countryCode = "DO"

        // Coordinates — approximate center of Zona Colonial
        latitude = 18.4720
        longitude = -69.8826
    }
}