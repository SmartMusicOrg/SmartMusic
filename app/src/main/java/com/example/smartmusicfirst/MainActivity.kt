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
import com.example.smartmusicfirst.broadcastReceivers.PhotoReceiver
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnectionListener
import com.example.smartmusicfirst.ui.views.SmartMusicScreen

const val DEBUG_TAG = "Debug"
var ImageNotificationUri: String? = null

class MainActivity : ComponentActivity(), SpotifyConnectionListener {

    private lateinit var receiver: PhotoReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extra = intent.getStringExtra("image")
        if (extra != null) {
            Log.d(DEBUG_TAG, "Received image from notification $extra")
            ImageNotificationUri = extra
        }
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                SmartMusicScreen(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                )
            }
        }
        SpotifyConnection.connect(this, this)
        receiver = PhotoReceiver()
    }

    override fun onStart() {
        super.onStart()
        if (!SpotifyConnection.isConnected()) {
            SpotifyConnection.connect(this, this)
        }
        receiver.registerContentObserver(this)
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
        receiver.unregisterContentObserver(this)
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
