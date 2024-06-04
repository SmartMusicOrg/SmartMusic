package com.example.smartmusicfirst.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.data.uiStates.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateEmail(newEmail: TextFieldValue) {
        _uiState.value = _uiState.value.copy(email = newEmail)
    }

    fun updateNotificationsEnabled(isEnabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = isEnabled)
    }

    fun updateOfflinePlaylistsEnabled(isEnabled: Boolean) {
        _uiState.value = _uiState.value.copy(offlinePlaylistsEnabled = isEnabled)
    }

    fun updateRockSelected(isSelected: Boolean) {
        _uiState.value = _uiState.value.copy(rockSelected = isSelected)
    }

    fun updatePopSelected(isSelected: Boolean) {
        _uiState.value = _uiState.value.copy(popSelected = isSelected)
    }

    fun updateJazzSelected(isSelected: Boolean) {
        _uiState.value = _uiState.value.copy(jazzSelected = isSelected)
    }

    fun updateClassicalSelected(isSelected: Boolean) {
        _uiState.value = _uiState.value.copy(classicalSelected = isSelected)
    }

    fun updateHipHopSelected(isSelected: Boolean) {
        _uiState.value = _uiState.value.copy(hipHopSelected = isSelected)
    }

    fun updateArtistsText(newText: TextFieldValue) {
        _uiState.value = _uiState.value.copy(artistsText = newText)
    }
}
