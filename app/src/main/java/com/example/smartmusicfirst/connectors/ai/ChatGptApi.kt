package com.example.smartmusicfirst.connectors.ai

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
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
        var res =
            "I have a music application that needs to generate playlists based on user input.\n" +
                    " The application should prioritize songs related to specific keywords over user preferences like favorite artists or genres.\n" +
                    " Here is the data provided by the user:\n\n" +
                    " - Keywords (ordered by significance): $keywordsTemplate\n"
        if (SpotifyWebApi.favoriteArtists.isNotEmpty()) {
            val artists = SpotifyWebApi.favoriteArtists.joinToString(separator = ", ") { it.name }
            res += " - Favorite artists: $artists\n"
            val genres = SpotifyWebApi.favoriteArtists.flatMap { it.genres }.distinct()
            res += " - Favorite genres: ${genres.joinToString(separator = ", ")}\n"
        }

        res += "Task: Create a list of 15 songs that most closely match the given keywords, even if they do not align with the user's favorite artists or genres.\n" +
                "Just try to take into consideration the user's preferences.\n" +
                " give me only the list and don't add further information"
        Log.d(DEBUG_TAG, "Ai Query: $res")
        return res
    }

    override fun getSongsNamesFromAiResponse(response: String): List<String> {
        val songs = mutableListOf<String>()
        try {
            val rows = response.split("\n")
            for (row in rows) {
                if (row.isNotEmpty()) {
                    var songToSearch = row.dropWhile { it != '.' && it != ' ' }
                    songToSearch = songToSearch.drop(2)
                    songToSearch = songToSearch.replace("\"", "")
                    songs.add(songToSearch)
                }
            }
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error during parsing AI response", e)
            throw e
        }
        return songs
    }
}