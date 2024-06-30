package com.example.smartmusicfirst.connectors.ai

interface AIApi {
    suspend fun getResponse(inputQuery: String, apiKey: String): String
}