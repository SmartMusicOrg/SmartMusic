package com.example.smartmusicfirst.connectors.ai

interface AIApi {
    suspend fun getResponse(inputQuery: String, apiKey: String): String
    fun buildQuery(keywords: List<String>): String
    fun getSongsNamesFromAiResponse(response: String): List<String>
}