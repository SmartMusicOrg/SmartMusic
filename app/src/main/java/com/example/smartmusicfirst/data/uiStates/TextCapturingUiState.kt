package com.example.smartmusicfirst.data.uiStates

import com.example.smartmusicfirst.data.LoadingHintsEnum

data class TextCapturingUiState(
    val inputString: String = "",
    val recordingGranted: Boolean = false,
    val isListening: Boolean = false,
    val canUseRecord: Boolean = true,
    val canUseSubmit: Boolean = true,
    val errorMessage: String = "",
    val isLoading: Boolean = false,
    val userHint: Int = LoadingHintsEnum.START.hintState
)
