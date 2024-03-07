package com.example.smartmusicfirst.connectors.spotify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.TAG
import com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import java.util.Properties


/**
 * The auth connection is responsible for the authenticating the user and fetching the
 * authorization code/access token that can subsequently be used to play music or in requests
 * to the API.
 */

/**
 * Listener interface for handling Spotify authentication events.
 * Implement this interface to receive notifications when the Spotify authentication is successful or fails.
 */
interface SpotifyAuthConnectionListener {
    fun onSpotifyAuthSuccess(accessToken: String)
    fun onSpotifyAuthFailure(error: Throwable)
}

/**
 * Singleton object responsible for managing the authentication to Spotify.
 */
object SpotifyAuthConnection {
    private var spotifyAuthConnectionListener: SpotifyAuthConnectionListener? = null

    // Spotify client ID and redirect URI
    private var clientId: String = ""
    private var redirectUri: String = ""

    /**
     * Initializes the Spotify authentication connection using the provided activity and notifies
     * the listener when the authentication is successful or fails.
     *
     * @param activity The activity used to establish the authentication.
     * @param listener The listener to be added to the list of SpotifyAuthConnectionListeners.
     */
    fun initAuthConnection(activity: Activity, listener: SpotifyAuthConnectionListener) {
        spotifyAuthConnectionListener = listener
        val properties = Properties()
        try {
            // Load the spotify_config.properties file from resources
            val inputStream = activity.resources.openRawResource(R.raw.spotify_config)
            properties.load(inputStream)

            // Read the client ID and redirect URI from the properties
            SpotifyAuthConnection.clientId = properties.getProperty("spotify_client_id")
            SpotifyAuthConnection.redirectUri = properties.getProperty("spotify_redirect_uri")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Spotify config: ${e.message}", e)
        }
        val requestCode = 1337

        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(arrayOf("user-read-email", "user-library-read"))
            .setCustomParam("show_dialog", "true")
            .build()

        AuthorizationClient.openLoginActivity(activity, requestCode, request)
    }

    /**
     * Handles the result of the authentication request.
     *
     * @param requestCode The request code used to start the authentication request.
     * @param resultCode The result code of the authentication request.
     * @param data The data returned from the authentication request.
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val response = AuthorizationClient.getResponse(resultCode, data)
        when (response.type) {
            AuthorizationResponse.Type.TOKEN -> {
                spotifyAuthConnectionListener?.onSpotifyAuthSuccess(response.accessToken)
            }

            AuthorizationResponse.Type.ERROR -> {
                spotifyAuthConnectionListener?.onSpotifyAuthFailure(Throwable(response.error))
            }

            else -> {
                spotifyAuthConnectionListener?.onSpotifyAuthFailure(Throwable("Invalid response type"))
            }
        }
    }
}