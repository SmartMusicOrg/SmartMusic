package com.example.smartmusicfirst.data.uiStates

import androidx.compose.ui.text.input.TextFieldValue

data class SettingsUiState(
    val notificationsEnabled: Boolean = false,
    val offlinePlaylistsEnabled: Boolean = false,
    val email: TextFieldValue = TextFieldValue("user@example.com"),
    val rockSelected: Boolean = false,
    val popSelected: Boolean = false,
    val jazzSelected: Boolean = false,
    val classicalSelected: Boolean = false,
    val hipHopSelected: Boolean = false,
    val artistsText: TextFieldValue = TextFieldValue("")
)
