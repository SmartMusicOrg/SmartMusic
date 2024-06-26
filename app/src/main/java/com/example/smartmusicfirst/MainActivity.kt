package com.example.smartmusicfirst

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
import com.example.smartmusicfirst.ui.views.SmartMusicScreen

const val DEBUG_TAG = "Debug"

class MainActivity : ComponentActivity(), SpotifyConnectionListener {


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
        SpotifyConnection.connect(this, this)
    }

    override fun onStart() {
        super.onStart()
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

    override fun onSpotifyConnected() {
    }
}

fun playPlaylist(playlistURI: String) {
    Log.d(DEBUG_TAG, "play playlist $playlistURI")
    SpotifyConnection.getPlayerApi()?.play(playlistURI)
}

fun playSong(songUri: String) {
    SpotifyConnection.getPlayerApi()?.play(songUri)
}
