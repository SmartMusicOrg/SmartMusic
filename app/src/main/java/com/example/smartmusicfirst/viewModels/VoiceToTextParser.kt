package com.example.smartmusicfirst.viewModels

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.data.uiStates.VoiceToTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VoiceToTextParser(
    private val application: Application,
    private val viewModel: TextCapturingViewModel
) : RecognitionListener {
    private val _state = MutableStateFlow(VoiceToTextState())
    val state = _state.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(application)

    fun startListening(languageCode: String = "en") {
        _state.update { VoiceToTextState() }
        viewModel.updateCanUseRecord(true)

        if (!SpeechRecognizer.isRecognitionAvailable(application)) {
            viewModel.updateInputString("Speech recognition is not available on this device")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _state.update { it.copy(isListening = true) }
    }

    fun stopListening() {
        recognizer.stopListening()
        _state.update { it.copy(isListening = false) }
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        _state.update { it.copy(spokenText = "") }
    }

    override fun onEndOfSpeech() {
        _state.update { it.copy(isListening = false) }
        viewModel.updateCanUseRecord(false)
        viewModel.updateListeningState(false)
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.e(TAG, "Error occurred: $errorMessage")
        _state.update { it.copy(isListening = false) }
        viewModel.updateInputString(errorMessage)
        if (errorCode != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            viewModel.updateCanUseRecord(true)
        }
        viewModel.updateCanUseSubmit(true)
    }

    override fun onResults(p0: Bundle?) {
        p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)?.let { result ->
            Log.d(TAG, "Result: $result")
            viewModel.updateInputString(result)
            viewModel.updateCanUseRecord(true)
            viewModel.updateCanUseSubmit(true)
        }
    }

    override fun onBeginningOfSpeech() {
        viewModel.updateCanUseSubmit(false)
        viewModel.updateListeningState(true)
    }

    override fun onRmsChanged(p0: Float) = Unit

    override fun onBufferReceived(p0: ByteArray?) = Unit

    override fun onPartialResults(p0: Bundle?) = Unit

    override fun onEvent(p0: Int, p1: Bundle?) = Unit

    private fun getErrorText(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client-side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Waiting for recognizer to finish"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }
}
