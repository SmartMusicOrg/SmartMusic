package com.example.smartmusicfirst.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TextCapturingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TextCapturingUiState())
    val uiState: StateFlow<TextCapturingUiState> = _uiState.asStateFlow()

    fun updateInputString(str: String) {
        _uiState.value = _uiState.value.copy(inputString = str)
    }

    fun getKeyword(accessToken: String = "") {
        try {
            Log.d(TAG, "Access token: $accessToken")
            CroticalioApi.findKeyWords(
                accessToken = accessToken,
                searchQuery = uiState.value.inputString,
                callback = { keywords ->
                    Log.d(TAG, "Keywords found:")
                    keywords.forEach {
                        Log.d(TAG, it.toString())
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
        }
    }

}
