package com.main.unicdaevento

import android.app.Application
import com.google.android.gms.maps.MapsInitializer

class UnicdaEventoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, null)
    }
}

