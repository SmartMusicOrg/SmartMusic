package com.example.smartmusicfirst.connectors.spotify

import android.content.Context
import android.util.Log
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.TAG
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.util.Properties


/**
 * Listener interface for handling Spotify connection events.
 * Implement this interface to receive notifications when the Spotify connection is established.
 * This is the interface that publish the onSpotifyConnected method.
 */
interface SpotifyConnectionListener {
    /**
     * Callback method invoked when the Spotify connection is successfully established.
     * this is the method that the observer will implement when the connection is established.
     */
    fun onSpotifyConnected()
}

/**
 * Singleton object responsible for managing the connection to Spotify.
 */
object SpotifyConnection {
    private var spotifyConnectionListener : SpotifyConnectionListener? = null
    private var connectionListener: Connector.ConnectionListener? = null

    // Spotify client ID and redirect URI
    private var clientId: String = ""
    private var redirectUri: String = ""

    // Instance of SpotifyAppRemote for managing the connection
    var spotifyAppRemote: SpotifyAppRemote? = null
        private set

    /**
     * Connects to the Spotify App Remote using the provided context and notifies
     * the listener when the connection is established.
     *
     * @param context The context used to establish the connection.
     * @param listener The listener to be added to the list of SpotifyConnectionListeners.
     */
    fun connect(context: Context, listener: SpotifyConnectionListener) {
        spotifyConnectionListener = listener
        val properties = Properties()
        try {
            // Load the spotify_config.properties file from resources
            val inputStream = context.resources.openRawResource(R.raw.spotify_config)
            properties.load(inputStream)

            // Read the client ID and redirect URI from the properties
            clientId = properties.getProperty("spotify_client_id")
            redirectUri = properties.getProperty("spotify_redirect_uri")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Spotify config: ${e.message}", e)
        }
        val connectionParams =
            ConnectionParams.Builder(clientId)
                .setRedirectUri(redirectUri)
                .showAuthView(true)
                .build()

        connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                // Notify the listener when the connection is established
                spotifyConnectionListener?.onSpotifyConnected()
            }

            override fun onFailure(throwable: Throwable?) {
                Log.e(TAG, "Connection failed: ${throwable?.message}", throwable)
            }
        }

        // Establish the connection to Spotify
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
    }

    /**
     * Disconnects from the Spotify App Remote and releases resources.
     */
    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
        }
    }

    /**
     * Checks if the Spotify connection is currently active.
     *
     * @return `true` if connected to Spotify, `false` otherwise.
     */
    fun isConnected(): Boolean {
        return spotifyAppRemote != null && spotifyAppRemote!!.isConnected
    }

    /**
     * Retrieves the PlayerApi instance from the Spotify App Remote.
     *
     * @return The PlayerApi instance, or null if not connected.
     */
    fun getPlayerApi(): PlayerApi? {
        return spotifyAppRemote?.playerApi
    }
}
