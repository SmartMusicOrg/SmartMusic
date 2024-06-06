package com.example.smartmusicfirst.viewModels

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.data.uiStates.SettingsUiState
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TextCapturingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TextCapturingUiState())
    val uiState: StateFlow<TextCapturingUiState> = _uiState.asStateFlow()

    fun updateInputString(str : String) {
        _uiState.value = _uiState.value.copy(inputString = str)
    }
}
