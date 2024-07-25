package com.example.smartmusicfirst.viewModels

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.ai.AIApi
import com.example.smartmusicfirst.connectors.ai.GeminiApi
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.datastore.DataStorePreferences
import com.example.smartmusicfirst.connectors.firebase.FirebaseApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.LoadingHintsEnum
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import com.example.smartmusicfirst.playPlaylist
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class TextCapturingViewModel(application: Application) : AndroidViewModel(application),
    RecognitionListener {
    private val app = application
    private val _uiState = MutableStateFlow(TextCapturingUiState())
    val uiState: StateFlow<TextCapturingUiState> = _uiState.asStateFlow()
    private var recognizer = SpeechRecognizer.createSpeechRecognizer(application)

    /**
     * Update the input string in the UI
     * @param str The new input string
     */
    fun updateInputString(str: String) {
        _uiState.value = _uiState.value.copy(inputString = str)
    }

    /**
     * Enable the recording button
     * @param isAvailable True if the recording is available
     */
    fun enableRecording(isAvailable: Boolean) {
        _uiState.value = _uiState.value.copy(recordingGranted = isAvailable)
    }

    fun updateCanUseSubmit(canUseSubmit: Boolean) {
        _uiState.value = _uiState.value.copy(canUseSubmit = canUseSubmit)
    }

    fun searchSong(
        corticalioAccessToken: String,
        aiApiKey: String,
        aiModel: AIApi = GeminiApi,
        onNavigateToPlayerPage: () -> Unit = {}
    ) {
        _uiState.value =
            _uiState.value.copy(canUseRecord = false, canUseSubmit = false, isLoading = true)
        viewModelScope.launch {
            val time = measureTimeMillis {
                try {
                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.KEYWORD_EXTRACT.hintState)
                    // ML-Kit turns hebrew to english if the text is in hebrew:
                    translateText(_uiState.value.inputString)

                    // Take the keywords
                    val keywords = CroticalioApi.findKeyWords(
                        corticalioAccessToken,
                        _uiState.value.inputString
                    )

                    if (keywords.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            canUseRecord = true,
                            canUseSubmit = true,
                            isLoading = false,
                            errorMessage = "Can not find keywords. Please try again."
                        )
                        return@launch
                    }

                    // Filter the keywords and get current user preferences

                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.GET_AI_OFFER.hintState)
                    // Give AI the keywords and extract relevant songs
                    val query = aiModel.buildQuery(keywords.map { it.word })

                    val response = aiModel.getResponse(query, aiApiKey)
                    Log.d(DEBUG_TAG, "Ai response: $response")

                    if (response.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            canUseRecord = true,
                            canUseSubmit = true,
                            errorMessage = "Can not find songs to play. Please try again."
                        )
                        return@launch
                    }

                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.SONGS_EXTRACT.hintState)
                    // Clean the response of Gemini
                    val songs = aiModel.getSongsNamesFromAiResponse(response)
                    Log.d(DEBUG_TAG, "Songs: $songs")

                    // Get the songs from Spotify
                    val songsList = SpotifyWebApi.getSongsList(songs)

                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.BUILD_PLAYLIST.hintState)
                    launch {
                        try {
                            val firebaseApi = FirebaseApi()
                            val queryDocRef = withContext(Dispatchers.IO) {
                                firebaseApi.addDoc(
                                    "users/${SpotifyWebApi.currentUser.id}/queries",
                                    mapOf(
                                        "isImage" to false,
                                        "text" to _uiState.value.inputString,
                                        "keywords" to keywords.joinToString(", ") { it.word },
                                        "aiResponse" to response,
                                        "songs" to songs.joinToString(", ")
                                    )
                                )
                            }
                            if (queryDocRef != null) {
                                songsList.forEach {
                                    withContext(Dispatchers.IO) {
                                        firebaseApi.addDoc(
                                            "users/${SpotifyWebApi.currentUser.id}/queries/${queryDocRef.id}/songs",
                                            mapOf(
                                                "uri" to it.uri,
                                                "name" to it.name,
                                                "artist" to it.album,
                                                "popularity" to it.popularity
                                            )
                                        )
                                    }
                                }
                            } else {
                                Log.e(DEBUG_TAG, "queryDocRef is null")
                            }
                        } catch (e: Exception) {
                            Log.e(DEBUG_TAG, "Error during saving query to Firebase", e)
                        }
                    }

                    DataStorePreferences.readData(app, stringPreferencesKey("lastPlaylistUri"))
                        .let {
                            if (it.isNotEmpty()) {
                                try {
                                    SpotifyWebApi.unfollowPlaylist(it)
                                } catch (e: Exception) {
                                    Log.e(DEBUG_TAG, "some error during unfollowing", e)
                                }
                            }
                        }
                    // Create a playlist that will contain all the songs
                    val playlist = SpotifyWebApi.createPlaylist(
                        SpotifyWebApi.currentUser.id,
                        "Smart Music First Playlist"
                    )

                    DataStorePreferences.saveData(
                        app,
                        stringPreferencesKey("lastPlaylistUri"),
                        playlist!!.id
                    )

                    // Add the songs to the playlist
                    val songUris = songsList.map { it.uri }
                    Log.d(DEBUG_TAG, "Song URIs: $songUris")

                    SpotifyWebApi.addItemsToExistingPlaylist(playlist.id, songUris)
                    Log.d(DEBUG_TAG, "Songs added to playlist")

                    // Play the playlist
                    playPlaylist(playlist.uri)
                    _uiState.value = _uiState.value.copy(
                        canUseRecord = true,
                        canUseSubmit = true,
                        isLoading = false
                    )

                    //me and my girlfriend having fun together
                } catch (e: Exception) {
                    Log.e(DEBUG_TAG, "Error during API calls", e)
                    _uiState.value = _uiState.value.copy(
                        canUseRecord = true,
                        canUseSubmit = true,
                        errorMessage = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
            Log.d(DEBUG_TAG, "Time taken: $time ms")
            onNavigateToPlayerPage()
        }
    }

    fun speechToTextButtonClicked() {
        if (_uiState.value.isListening) {
            stopListening()
        } else {
            startListening(Locale.current.language)
        }
    }

    private suspend fun translateText(
        text: String
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.HEBREW)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        val translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        val download = translator.downloadModelIfNeeded(conditions)
        download.await()
        if (download.isSuccessful) {
            val result = translator.translate(text).await()
            Log.d(DEBUG_TAG, "Translated text: $result")
            updateInputString(result)
        } else {
            Log.e(DEBUG_TAG, "Error during translation")
        }
    }

    private fun startListening(languageCode: String = "en") {
        _uiState.value = _uiState.value.copy(canUseRecord = true)

        if (!SpeechRecognizer.isRecognitionAvailable(app)) {
            this.updateInputString("Speech recognition is not available on this device")
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1700L)
        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)
        _uiState.value =
            _uiState.value.copy(canUseRecord = true, canUseSubmit = false, isListening = true)
    }

    private fun stopListening() {
        if (_uiState.value.isListening) {
            _uiState.value = _uiState.value.copy(isListening = false, canUseRecord = false)
            Handler(Looper.getMainLooper()).postDelayed({
                recognizer.stopListening()
                recognizer.destroy()
                _uiState.value = _uiState.value.copy(canUseSubmit = true)
                recognizer = SpeechRecognizer.createSpeechRecognizer(app).apply {
                    setRecognitionListener(this@TextCapturingViewModel)
                }
                _uiState.value = _uiState.value.copy(canUseRecord = true)
            }, 2000)
        } else {
            Log.d(DEBUG_TAG, "stopListening called but recognizer is not listening")
        }
    }

    override fun onReadyForSpeech(p0: Bundle?) {
    }

    override fun onEndOfSpeech() {
        _uiState.value = _uiState.value.copy(canUseRecord = false)
        _uiState.value = _uiState.value.copy(isListening = false)
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.e(DEBUG_TAG, "Error occurred: $errorMessage")
        _uiState.value = _uiState.value.copy(errorMessage = errorMessage)
        if (errorCode != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            _uiState.value = _uiState.value.copy(canUseRecord = true, isListening = false)
        }
        _uiState.value = _uiState.value.copy(canUseSubmit = true)
    }

    override fun onResults(p0: Bundle?) {
        p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)?.let { result ->
            Log.d(DEBUG_TAG, "Result: $result")
            this.updateInputString(result)
            _uiState.value = _uiState.value.copy(canUseRecord = true, canUseSubmit = true)
        }
    }

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

    override fun onCleared() {
        super.onCleared()
        recognizer.destroy()
    }

    override fun onBeginningOfSpeech() = Unit

    override fun onRmsChanged(p0: Float) = Unit

    override fun onBufferReceived(p0: ByteArray?) = Unit

    override fun onPartialResults(p0: Bundle?) {
        p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.getOrNull(0)?.let { result ->
            Log.d(DEBUG_TAG, "Result: $result")
            this.updateInputString(result)
        }
    }

    override fun onEvent(p0: Int, p1: Bundle?) = Unit

}
