package com.example.smartmusicfirst.connectors.ai

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.DEBUG_TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


object ChatGptApi : AIApi {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    override suspend fun getResponse(inputQuery: String, apiKey: String): String = withContext(
        Dispatchers.IO
    ) {
        suspendCancellableCoroutine { continuation ->
            val url = "https://api.openai.com/v1/chat/completions"
            val body = JSONObject().apply {
                put("model", "gpt-3.5-turbo")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", inputQuery)
                    })
                })
            }

            val request = object : JsonObjectRequest(Method.POST, url, body,
                Response.Listener { response ->
                    continuation.resume(
                        response.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                            .getString("content")
                    )
                }, {
                    continuation.resumeWithException(it)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf(
                        "Authorization" to "Bearer $apiKey",
                        "Content-Type" to "application/json"
                    )
                }
            }
            Volley.newRequestQueue(applicationContext).add(request)
        }
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