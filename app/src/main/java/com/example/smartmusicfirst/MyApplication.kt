package com.example.smartmusicfirst

import android.app.Application
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the APIs with the application context
        SpotifyWebApi.init(this, "")
        CroticalioApi.init(this)
    }
}
