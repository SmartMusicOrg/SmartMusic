package com.example.smartmusicfirst.connectors.spotify

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.firebase.FirebaseApi
import com.example.smartmusicfirst.models.SpotifyArtist
import com.example.smartmusicfirst.models.SpotifyPlaylist
import com.example.smartmusicfirst.models.SpotifySong
import com.example.smartmusicfirst.models.SpotifyUser
import kotlinx.coroutines.CompletableDeferred
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.coroutines.suspendCoroutine

object SpotifyWebApi {
    private lateinit var applicationContext: Context

    lateinit var accessToken: String
    lateinit var currentUser: SpotifyUser
    lateinit var favoriteArtists: List<SpotifyArtist>

    suspend fun init(context: Context, accessToken: String) {
        val firebaseApi = FirebaseApi()
        applicationContext = context.applicationContext
        this.accessToken = accessToken
        currentUser = this.getCurrentUserDetails()
        favoriteArtists = this.getFollowedArtists()
        try {
            if (!firebaseApi.isExist("users", currentUser.id)) {
                firebaseApi.createDoc(
                    "users", currentUser.id, mapOf(
                        "displayName" to currentUser.displayName,
                        "imageUrl" to currentUser.images.firstOrNull(),
                        "product" to currentUser.product
                    )
                )
            }
            // adding sub collection for favorite artists to user doc:
            favoriteArtists.forEach { artist ->
                firebaseApi.createDoc(
                    "users/${currentUser.id}/favoriteArtists", artist.uri, mapOf(
                        "name" to artist.name,
                        "genres" to artist.genres
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error adding user to firebase: ${e.message}", e)
        }
    }

    fun searchForPlaylist(playlistName: String, callback: (String) -> Unit) {
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
                Log.e(DEBUG_TAG, "Error fetching playlist: ${error.message}", error)
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

    fun searchForSong(songName: String, callback: (List<SpotifySong>) -> Unit) {
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
                Log.e(DEBUG_TAG, "Error fetching song: ${error.message}", error)
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

    fun createPlaylist(userId: String, playlistName: String, callback: (SpotifyPlaylist?) -> Unit) {
        val url = "https://api.spotify.com/v1/users/$userId/playlists"
        val body = JSONObject().apply {
            put("name", playlistName)
            put("description", "Temporary playlist created by SmartMusicFirst app")
            put("public", false)
        }
        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            body,
            Response.Listener { response ->
                try {
                    val playlist = SpotifyPlaylist(
                        uri = response.getString("uri"),
                        name = response.getString("name"),
                        imageUrl = response.optJSONArray("images")?.optJSONObject(0)
                            ?.optString("url", ""),
                        id = response.getString("id")
                    )
                    Log.d(DEBUG_TAG, "Playlist: $playlist")
                    callback(playlist)
                } catch (e: JSONException) {
                    Log.e(DEBUG_TAG, "Error parsing playlist response: ${e.message}", e)
                    callback(null)
                }
            },
            Response.ErrorListener { error ->
                Log.e(DEBUG_TAG, "Error creating playlist: ${error.message}", error)
                callback(null)
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Add the request to the request queue
        Volley.newRequestQueue(applicationContext).add(request)
    }

    fun addItemsToExistingPlaylist(
        playlistId: String,
        songUris: List<String>
    ): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        val url = "https://api.spotify.com/v1/playlists/$playlistId/tracks"
        val body = JSONObject().apply {
            put("uris", JSONArray(songUris))
        }
        val request = object : JsonObjectRequest(
            Method.POST,
            url,
            body,
            Response.Listener { response ->
                Log.d(DEBUG_TAG, "Items added to playlist: $response")
                deferred.complete(Unit) // Signal completion
            },
            Response.ErrorListener { error ->
                Log.e(DEBUG_TAG, "Error adding items to playlist: ${error.message}", error)
                deferred.completeExceptionally(Exception("Error adding items to playlist: ${error.message}"))
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        Volley.newRequestQueue(applicationContext).add(request)
        return deferred
    }

    private suspend fun getCurrentUserDetails(): SpotifyUser = suspendCoroutine { continuation ->
        val url = "https://api.spotify.com/v1/me"
        val request = object : JsonObjectRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response ->
                try {
                    val user = SpotifyUser(
                        uri = response.getString("uri"),
                        id = response.getString("id"),
                        displayName = response.getString("display_name"),
                        images = response.getJSONArray("images").let { images ->
                            List(images.length()) { images.getJSONObject(it).getString("url") }
                        },
                        product = response.getString("product")
                    )
                    Log.d(DEBUG_TAG, "User: $user")
                    continuation.resumeWith(Result.success(user))
                } catch (e: JSONException) {
                    Log.e(DEBUG_TAG, "Error parsing user details: ${e.message}", e)
                    continuation.resumeWith(Result.failure(e))
                }
            },
            Response.ErrorListener { error ->
                Log.e(DEBUG_TAG, "Error fetching user details: ${error.message}", error)
                continuation.resumeWith(Result.failure(error))
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $accessToken"
                return headers
            }
        }

        Volley.newRequestQueue(applicationContext).add(request)
    }

    private suspend fun getFollowedArtists(): List<SpotifyArtist> =
        suspendCoroutine { continuation ->
            val url = "https://api.spotify.com/v1/me/following?type=artist&limit=5"
            val request = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener { response ->
                    try {
                        val artists = mutableListOf<SpotifyArtist>()
                        val responseItems = response.getJSONObject("artists").getJSONArray("items")
                        for (i in 0 until responseItems.length()) {
                            val item = responseItems.getJSONObject(i)
                            val artist = SpotifyArtist(
                                uri = item.getString("uri"),
                                name = item.getString("name"),
                                genres = item.getJSONArray("genres").let { genres ->
                                    List(genres.length()) { genres.getString(it) }
                                }
                            )
                            artists.add(artist)
                        }
                        Log.d(DEBUG_TAG, "Artists: $artists")
                        continuation.resumeWith(Result.success(artists))
                    } catch (e: JSONException) {
                        Log.e(DEBUG_TAG, "Error parsing followed artists: ${e.message}", e)
                        continuation.resumeWith(Result.failure(e))
                    }
                },
                Response.ErrorListener { error ->
                    Log.e(DEBUG_TAG, "Error fetching followed artists: ${error.message}", error)
                    continuation.resumeWith(Result.failure(error))
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
