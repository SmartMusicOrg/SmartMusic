package com.example.smartmusicfirst.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.playPlaylist

class ButtonEmotionViewModel : ViewModel() {
    fun onEmotionButtonClicked(emotion: String, onNavigateToPlayerPage: () -> Unit = {}) {
        SpotifyWebApi.searchForPlaylist("$emotion mood") { playlistId ->
            if (playlistId.isNotEmpty()) {
                playPlaylist(playlistId)
                onNavigateToPlayerPage()
            } else {
                Log.e(DEBUG_TAG, "No playlist found")
            }
        }
    }
}