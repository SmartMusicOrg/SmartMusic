package com.example.smartmusicfirst.viewModels

import android.content.Context
import android.content.Intent
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

    fun sendButtonClicked(context: Context) {
        val i = Intent(Intent.ACTION_SEND)
        val emailAddress = arrayOf(uiState.value.receivingAddress)
        i.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        i.putExtra(Intent.EXTRA_SUBJECT, uiState.value.emailSubject)
        i.putExtra(Intent.EXTRA_TEXT, uiState.value.emailBody)
        i.setType("message/rfc822")
        context.startActivity(Intent.createChooser(i, "Choose an Email client : "))
        _uiState.value = ContactUiState()
    }
}