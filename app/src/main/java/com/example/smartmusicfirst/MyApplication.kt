package com.example.smartmusicfirst

import android.app.Application
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the APIs with the application context
        CroticalioApi.init(this)
    }
}
