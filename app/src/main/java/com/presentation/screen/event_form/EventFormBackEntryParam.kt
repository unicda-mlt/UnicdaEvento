package com.presentation.screen.event_form

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize


@Parcelize
sealed class EventFormBackEntryParam : Parcelable {

    abstract val paramName: String

    data class Location(
        val latLng: LatLng,
        val locationDisplayName: String
    ) : EventFormBackEntryParam() {

        override val paramName: String
            get() = PARAM_NAME

        companion object {
            const val PARAM_NAME = "location"
        }

    }

}