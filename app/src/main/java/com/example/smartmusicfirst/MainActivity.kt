package com.example.smartmusicfirst

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyAuthConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.ui.views.SmartMusicScreen

const val TAG = "MainActivity"
var accessToken: String = ""

class MainActivity : ComponentActivity(), SpotifyConnectionListener, SpotifyAuthConnectionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SmartMusicScreen(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
        }
        SpotifyAuthConnection.initAuthConnection(this, this)
        // Initialize SpotifyWebApi with the application context
        SpotifyWebApi.init(applicationContext)
    }

    override fun onSpotifyConnected() {
        Log.d(TAG, "Spotify connected")
//        connected()
    }

    // TODO to remove the onStart:
    override fun onStart() {
        super.onStart()
        Log.d(TAG, accessToken)
        if (!SpotifyConnection.isConnected()) {
            SpotifyConnection.connect(this, this)
        }
    }

    override fun onStop() {
        super.onStop()
        SpotifyConnection.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pause the music before disconnecting
        SpotifyConnection.getPlayerApi()?.pause()
        SpotifyConnection.disconnect()
    }

    override fun onSpotifyAuthSuccess(accessToken: String) {
        Log.d(TAG, "Spotify authentication successful, Access Token: $accessToken")
        com.example.smartmusicfirst.accessToken = accessToken
        SpotifyConnection.connect(this, this)
    }

    override fun onSpotifyAuthFailure(error: Throwable) {
        Log.e(TAG, "Spotify authentication failed: ${error.message}", error)
    }

    // TODO change the onActivityResult to better handle the SpotifyAuthConnection
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        SpotifyAuthConnection.onActivityResult(requestCode, resultCode, data)
    }
}

fun playPlaylist(playlistId: String) {
    // Play a playlist
    Log.d(TAG, "Playing playlist with ID: $playlistId")
    val playlistURI = "spotify:playlist:$playlistId"
    SpotifyConnection.getPlayerApi()?.play(playlistURI)
    // Subscribe to PlayerState if needed
}

fun playSong(songUri: String){
    SpotifyConnection.getPlayerApi()?.play(songUri)
}
