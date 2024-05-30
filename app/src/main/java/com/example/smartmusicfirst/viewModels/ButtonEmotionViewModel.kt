package com.example.smartmusicfirst.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.accessToken
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.playPlaylist

class ButtonEmotionViewModel: ViewModel(){
    fun onEmotionButtonClicked(emotion: String){
        SpotifyWebApi.searchForPlaylist("$emotion mood", accessToken) { playlistId ->
            if (playlistId.isNotEmpty()) {
                playPlaylist(playlistId)
            } else {
                Log.e(TAG, "No playlist found")
            }
        }
    }
}