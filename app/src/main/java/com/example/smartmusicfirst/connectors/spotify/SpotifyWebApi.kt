package com.example.smartmusicfirst.connectors.spotify

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.models.SpotifySong
import java.net.URLEncoder

object SpotifyWebApi {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun searchForPlaylist(
        playlistName: String,
        accessToken: String,
        callback: (String) -> Unit
    ) {
        val encodedPlaylistName = URLEncoder.encode(playlistName, "UTF-8")
        val url = "https://api.spotify.com/v1/search?q=$encodedPlaylistName&type=playlist&limit=3"
        val randomIndex = (0..2).random()
        val request = object : JsonObjectRequest(Method.GET, url, null,
            Response.Listener { response ->
                // Handle successful response
                val playlistUri = response.getJSONObject("playlists")
                    .getJSONArray("items")
                    .takeIf { it.length() > 0 }
                    ?.getJSONObject(randomIndex)
                    ?.getString("uri") ?: ""
                callback(playlistUri)
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
        Volley.newRequestQueue(applicationContext).add(request)

    }

    fun searchForSong(
        songName: String,
        accessToken: String,
        callback: (List<SpotifySong>) -> Unit
    ) {
        val encodedSongName = URLEncoder.encode(songName, "UTF-8")
        val url = "https://api.spotify.com/v1/search?q=$encodedSongName&type=track&limit=5"

        val request = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response ->
                val spotifySongs = mutableListOf<SpotifySong>()
                val responseItems = response.getJSONObject("tracks").getJSONArray("items")
                for (i in 0 until responseItems.length()) {
                    val item = responseItems.getJSONObject(i)
                    val song = SpotifySong(
                        uri = item.getString("uri"),
                        name = item.getString("name"),
                        artistsUri = item.getJSONArray("artists").let { artists ->
                            List(artists.length()) { artists.getJSONObject(it).getString("uri") }
                        },
                        album = item.getJSONObject("album").getString("name"),
                        imageUrl = item.getJSONObject("album").getJSONArray("images")
                            .getJSONObject(0).getString("url"),
                        popularity = item.getInt("popularity")
                    )
                    spotifySongs.add(song)
                }
                callback(spotifySongs)
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "Error fetching song: ${error.message}", error)
                callback(emptyList())
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }

        // Add the request to the request queue
        Volley.newRequestQueue(applicationContext).add(request)
    }

}