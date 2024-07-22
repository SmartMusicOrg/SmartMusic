package com.example.smartmusicfirst.viewModels

import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.connectors.spotify.SpotifyConnection
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.uiStates.PlayerPageUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerPageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerPageUiState())
    val uiState = _uiState.asStateFlow()

    fun togglePlay() {
        if (_uiState.value.isPlaying) SpotifyConnection.getPlayerApi()!!.pause()
        else SpotifyConnection.getPlayerApi()!!.resume()
        _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
    }

    fun isUserPremium(): Boolean {
        return SpotifyWebApi.currentUser.product == "premium"
    }
}
