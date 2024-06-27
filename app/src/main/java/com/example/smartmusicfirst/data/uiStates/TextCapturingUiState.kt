package com.example.smartmusicfirst.data.uiStates

data class TextCapturingUiState(
    val inputString: String = "",
    val recordingGranted: Boolean = false,
    val isListening: Boolean = false,
    val canUseRecord: Boolean = true,
    val canUseSubmit: Boolean = true,
    val errorMessage: String = ""
)
