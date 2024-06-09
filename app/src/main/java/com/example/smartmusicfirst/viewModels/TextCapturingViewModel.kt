package com.example.smartmusicfirst.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.gemini.GeminiApi
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import com.example.smartmusicfirst.models.KeywordCroticalio
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextCapturingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TextCapturingUiState())
    val uiState: StateFlow<TextCapturingUiState> = _uiState.asStateFlow()

    fun updateInputString(str: String) {
        _uiState.value = _uiState.value.copy(inputString = str)
    }

    private fun getKeywordDeferred(accessToken: String): CompletableDeferred<List<KeywordCroticalio>> {
        val deferred = CompletableDeferred<List<KeywordCroticalio>>()

        CroticalioApi.findKeyWords(
            accessToken = accessToken,
            searchQuery = _uiState.value.inputString,
            callback = { keywords ->
                deferred.complete(keywords)
            },
            errorCallback = { error ->
                deferred.completeExceptionally(error)
            }
        )

        return deferred
    }

    private fun getGeminiResponseDeferred(
        apiKey: String,
        listOfKeywords: List<KeywordCroticalio>
    ): CompletableDeferred<String> {
        val deferred = CompletableDeferred<String>()
        val keywordsTemplate = listOfKeywords.joinToString(" ") { it.word }
        Log.d(TAG, "Keywords template: $keywordsTemplate")
        val inputQuery =
            "give me list of top five popular songs that connected to the following words: $keywordsTemplate give only the list of names and don't add further information"

        viewModelScope.launch {
            try {
                val response = GeminiApi.getGeminiResponse(inputQuery, apiKey)
                deferred.complete(response)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }
        return deferred
    }

    fun searchSong(corticalioAccessToken: String, geminiApiKey: String) {
        viewModelScope.launch {
            try {
                val keywords = getKeywordDeferred(corticalioAccessToken).await()
                val response = getGeminiResponseDeferred(geminiApiKey, keywords).await()
                Log.d(TAG, "Gemini Response: $response")
            } catch (e: Exception) {
                Log.e(TAG, "Error during API calls", e)
            }
        }
    }
}
