package com.flow.cms.screen.choose_location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.versionedparcelable.VersionedParcelize
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.presentation.screen.event_form.EventFormBackEntryParam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ChooseLocationScreenViewModel @Inject constructor(
    private val placesClient: PlacesClient
) : ViewModel() {
    sealed class ResponseState<out T> {
        object Idle : ResponseState<Nothing>()
        object Loading : ResponseState<Nothing>()
        data class Error(val error: Throwable) : ResponseState<Nothing>()
        data class Success<R>(val data: R) : ResponseState<R>()
    }

    data class GotAddress(val address: Address, val poiName: String? = null)

    @VersionedParcelize
    data class PoiItem(
        val name: String,
        val address: String,
        val placeId: String
    )

    private var _appContext: Context? = null

    private val _markerAddressDetail = MutableStateFlow<ResponseState<GotAddress>>(ResponseState.Idle)
    val markerAddressDetail = _markerAddressDetail.asStateFlow()

    private val _poiList = MutableStateFlow<List<PoiItem>>(emptyList())
    val poiList = _poiList.asStateFlow()

    private val _search = MutableStateFlow<String?>(null)
    val search: StateFlow<String?> = _search.asStateFlow()

    private val _latestLocation = MutableStateFlow<EventFormBackEntryParam.Location?>(null)

    init {
        listenSearch()
    }

    fun setContext(ctx: Context) {
        _appContext = ctx
    }

    @OptIn(FlowPreview::class)
    fun listenSearch() {
        viewModelScope.launch {
            search
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (_appContext == null) {
                        _markerAddressDetail.value =
                            ResponseState.Error(IllegalStateException("Context not present"))
                        _search.value = null
                        _poiList.value = emptyList()
                    }
                    else {
                        searchLocationByName(name = query, context = _appContext!!)
                    }
                }
        }
    }

    fun updateSearch(value: String?) {
        _search.value = value
    }

    fun updateLatestLatLng(value: EventFormBackEntryParam.Location?) {
        _latestLocation.value = value
    }

    fun getLatestLocation(): EventFormBackEntryParam.Location? {
        return _latestLocation.value
    }

    fun getMarkerAddressDetails(
        latLng: LatLng,
        poiName: String? = null
    ) {
        if (!Geocoder.isPresent()) {
            _markerAddressDetail.value =
                ResponseState.Error(IllegalStateException("Geocoder not available"))
            return
        }

        if (_appContext == null) {
            _markerAddressDetail.value =
                ResponseState.Error(IllegalStateException("Context not present"))
            return
        }

        val geocoder = Geocoder(_appContext!!, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val first = addresses.firstOrNull()
                        _markerAddressDetail.value =
                            first?.let { ResponseState.Success(GotAddress(
                                address = it,
                                poiName = poiName
                            )) }
                                ?: ResponseState.Error(Exception("Address not found"))
                    }

                    override fun onError(errorMessage: String?) {
                        _markerAddressDetail.value =
                            ResponseState.Error(Exception(errorMessage ?: "Geocoder error"))
                    }
                }
            )
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val first = addresses?.firstOrNull()

                    _markerAddressDetail.value =
                        first?.let { ResponseState.Success(GotAddress(
                            address = it,
                            poiName = poiName
                        )) }
                            ?: ResponseState.Error(Exception("Address not found"))
                } catch (e: Exception) {
                    _markerAddressDetail.value = ResponseState.Error(e)
                }
            }
        }
    }

    fun searchLocationByName(
        name: String?,
        context: Context
    ) {
        if (name.isNullOrBlank()) {
            _poiList.value = emptyList()
            return
        }

        if (!Geocoder.isPresent()) {
            _poiList.value = emptyList()
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(name)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _poiList.value = response.autocompletePredictions.map { p ->
                    PoiItem(
                        name = p.getPrimaryText(null).toString(),
                        address = p.getSecondaryText(null).toString(),
                        placeId = p.placeId
                    )
                }
            }
            .addOnFailureListener {
                _poiList.value = emptyList()
            }
    }

    fun getPoiLatLng(
        placeId: String,
        onResult: (latLng: LatLng?, name: String?) -> Unit
    ) {
        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.LOCATION,
            Place.Field.DISPLAY_NAME
        )

        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                onResult(place.location, place.displayName)
            }
            .addOnFailureListener { exception ->
                onResult(null, null)
            }
    }
}