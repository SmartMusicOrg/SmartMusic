package com.example.smartmusicfirst

import android.app.Application
import com.example.smartmusicfirst.connectors.ai.ChatGptApi
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.services.NotificationCenter

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the APIs with the application context
        CroticalioApi.init(this)
        ChatGptApi.init(this)
        NotificationCenter.init(this)
    }
}
