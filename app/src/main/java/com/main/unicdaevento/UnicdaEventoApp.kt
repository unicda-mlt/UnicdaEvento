package com.main.unicdaevento

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.example.unicdaevento.R


@HiltAndroidApp
class UnicdaEventoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)
        FirebaseApp.initializeApp(this)

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(
                this,
                getString(R.string.google_maps_key)
            )
        }
    }
}