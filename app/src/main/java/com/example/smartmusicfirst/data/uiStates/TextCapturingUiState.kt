package com.example.smartmusicfirst.data.uiStates

import androidx.compose.ui.text.input.TextFieldValue

data class TextCapturingUiState(
    val inputString: String = "",
    val canUseRecord: Boolean = false,
    val isListening: Boolean = false,
)
