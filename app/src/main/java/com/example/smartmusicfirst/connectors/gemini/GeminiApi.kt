package com.example.smartmusicfirst.connectors.gemini

import android.util.Log
import com.example.smartmusicfirst.TAG
import com.google.ai.client.generativeai.GenerativeModel


object GeminiApi {
    suspend fun getGeminiResponse(inputQuery: String, apiKey: String): String {
        var res = ""
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
        try {
            val response = generativeModel.generateContent(inputQuery)
            res = response.text ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error getting response: ${e.message}", e)
        }
        return res
    }
}
