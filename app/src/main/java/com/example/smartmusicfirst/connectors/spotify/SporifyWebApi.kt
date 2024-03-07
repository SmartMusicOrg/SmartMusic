package com.example.smartmusicfirst.connectors.spotify

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.TAG

object SporifyWebApi {

    fun searchForPlaylist(context:Context, playlistName: String, accessToken: String, callback: (String) -> Unit) {
        val url = "https://api.spotify.com/v1/search?q=$playlistName&type=playlist&limit=1"
        var playlistId = ""

        val request = object : JsonObjectRequest(Method.GET, url, null,
            Response.Listener { response ->
                // Handle successful response
                playlistId = response.getJSONObject("playlists")
                    .getJSONArray("items")
                    .takeIf { it.length() > 0 }
                    ?.getJSONObject(0)
                    ?.getString("id") ?: ""
                callback(playlistId)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error fetching playlist: ${error.message}", error)
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }

        // Add the request to the request queue
        Volley.newRequestQueue(context).add(request)

    }

}