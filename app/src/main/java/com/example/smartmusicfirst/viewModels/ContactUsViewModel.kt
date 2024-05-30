package com.example.smartmusicfirst.viewModels

import androidx.lifecycle.ViewModel
import com.example.smartmusicfirst.data.uiStates.ContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactUsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ContactUiState())
    val uiState: StateFlow<ContactUiState> = _uiState.asStateFlow()

    fun updateSubject(subject: String) {
        _uiState.value = _uiState.value.copy(emailSubject = subject)
    }

    fun updateBody(body: String) {
        _uiState.value = _uiState.value.copy(emailBody = body)
    }
}