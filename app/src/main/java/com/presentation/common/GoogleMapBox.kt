package com.presentation.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun GoogleMapBox(
    modifier: Modifier = Modifier,
    lat: Double,
    lng: Double,
    zoom: Float = 14f,
    showMarker: Boolean = true,
) {
    val target = remember(lat, lng) { LatLng(lat, lng) }
    val markerState = remember(target) { MarkerState(target) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(target, zoom)
    }

    var uiReady by remember { mutableStateOf(false) }

    LaunchedEffect (target, zoom) {
        withFrameNanos { }
        withFrameNanos { }
        uiReady = true
    }

    Box(modifier) {
        if (uiReady) {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false
                )
            ) {
                if (showMarker) {
                    AdvancedMarker(
                        state = markerState,
                        title = "Location"
                    )
                }
            }
        }
        else {
            CustomCircularProgressIndicator()
        }
    }
}

@Composable
fun MapPreloader() {
    AndroidView(
        modifier = Modifier.size(1.dp).alpha(0f),
        factory = { ctx ->
            MapView(ctx).apply {
                onCreate(null)
                getMapAsync { }
                onResume()
            }
        },
        onRelease = { v ->
            v.onPause()
            v.onDestroy()
        }
    )
}