package com.example.smartmusicfirst.connectors.ai

import android.util.Log
import com.example.smartmusicfirst.DEBUG_TAG
import com.google.ai.client.generativeai.GenerativeModel


object GeminiApi : AIApi {

    override suspend fun getResponse(inputQuery: String, apiKey: String): String {
        var res = ""
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey
        )
        try {
            val response = generativeModel.generateContent(inputQuery)
            res = response.text ?: ""
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error getting response: ${e.message}", e)
            throw e
        }
        return res
    }

    override fun buildQuery(keywords: List<String>): String {
        val keywordsTemplate = keywords.joinToString(separator = ", ")
        Log.d(DEBUG_TAG, "Keywords template: $keywordsTemplate")
        return "give me list of top fifteen popular songs that connected to the following words: $keywordsTemplate give only the list without any other world accept the list"
    }

    override fun getSongsNamesFromAiResponse(response: String): List<String> {
        val songs = mutableListOf<String>()
        try {
            val rows = response.split("\n")
            for (row in rows) {
                if (row.isNotEmpty()) {
                    row.replace("\"", "")
                    row.subSequence(2, row.length).toString().let {
                        songs.add(it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error during parsing AI response", e)
            throw e
        }
        return songs
    }
}
