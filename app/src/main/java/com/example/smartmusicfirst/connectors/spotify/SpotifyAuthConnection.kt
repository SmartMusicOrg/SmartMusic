package com.example.smartmusicfirst.connectors.spotify

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.TAG
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import java.util.Properties

interface SpotifyAuthConnectionListener {
    fun onSpotifyAuthSuccess(accessToken: String)
    fun onSpotifyAuthFailure(error: Throwable)
}

/**
 * Singleton object responsible for managing the authentication to Spotify.
 */
object SpotifyAuthConnection {
    private var spotifyAuthConnectionListener: SpotifyAuthConnectionListener? = null

    private var clientId: String = ""
    private var redirectUri: String = ""

    /**
     * Initializes the Spotify authentication connection using the provided activity and notifies
     * the listener when the authentication is successful or fails.
     *
     * @param activity The activity used to establish the authentication.
     * @param listener The listener to be added to the list of SpotifyAuthConnectionListeners.
     * @param launcher The launcher used to start the authentication activity.
     */
    fun initAuthConnection(
        activity: Activity,
        listener: SpotifyAuthConnectionListener,
        launcher: ActivityResultLauncher<Intent>
    ) {
        spotifyAuthConnectionListener = listener
        val properties = Properties()
        try {
            val inputStream = activity.resources.openRawResource(R.raw.spotify_config)
            properties.load(inputStream)
            clientId = properties.getProperty("spotify_client_id") ?: ""
            redirectUri = properties.getProperty("spotify_redirect_uri") ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Spotify config: ${e.message}", e)
        }

        val request = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN,
            redirectUri
        )
            .setScopes(arrayOf("playlist-modify-public","playlist-modify-private","user-read-private", "user-read-email", "user-library-read"))
            .setCustomParam("show_dialog", "true")
            .build()

        launcher.launch(AuthorizationClient.createLoginActivityIntent(activity, request))
    }

    /**
     * Handles the result of the authentication request.
     *
     * @param resultCode The result code of the authentication request.
     * @param data The data returned from the authentication request.
     */
    fun onActivityResult(resultCode: Int, data: Intent?) {
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
