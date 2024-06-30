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
import com.example.smartmusicfirst.models.KeywordCroticalio
import com.example.smartmusicfirst.models.SpotifySong
import com.example.smartmusicfirst.playPlaylist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun searchSong(corticalioAccessToken: String, aiApiKey: String, aiModel: AIApi = GeminiApi) {
        _uiState.value =
            _uiState.value.copy(canUseRecord = false, canUseSubmit = false, isLoading = true)
        viewModelScope.launch {
            val time = measureTimeMillis {
                try {
                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.KEYWORD_EXTRACT.hintState)
                    // Take the keywords
                    val keywords = CroticalioApi.findKeyWords(
                        corticalioAccessToken,
                        _uiState.value.inputString
                    )

                    // Filter the keywords and get current user preferences

                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.GET_AI_OFFER.hintState)
                    // Give Gemini the keywords and extract relevant songs
                    val query = buildQuery(keywords)

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
                    val songs = getSongsNamesFromAiResponse(response)
                    Log.d(DEBUG_TAG, "Songs: $songs")

                    // Get the songs from Spotify
                    val songsList = getSongsList(songs)

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
                                        "geminiResponse" to response,
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
        }
    }

    /**
     * Build the query for the AI model
     * @param keywords The keywords to use
     * @return The query
     */
    private fun buildQuery(keywords: List<KeywordCroticalio>): String {
        val keywordsTemplate = keywords.joinToString(" ") { it.word }
        Log.d(DEBUG_TAG, "Keywords template: $keywordsTemplate")
        return "give me list of top fifteen popular songs that connected to the following words: $keywordsTemplate give only the list without any other world accept the list"
    }

    /**
     * Get the songs names from the AI response
     * @param response The response from the AI model
     * @return The list of songs
     */
    private fun getSongsNamesFromAiResponse(response: String): List<String> {
        val songs = mutableListOf<String>()
        try {
            val rows = response.split("\n")
            for (row in rows) {
                if (row.isNotEmpty()) {
                    row.replace("\"", "")
                    row.subSequence(2, row.length).toString().let {
                        songs.add(it)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "Error during parsing AI response", e)
            _uiState.value =
                _uiState.value.copy(errorMessage = "Can not find songs to play. Please try again.")
        }
        return songs
    }

    private suspend fun getSongsList(songs: List<String>): List<SpotifySong> = coroutineScope {
        val songsList = songs.map { songName ->
            async {
                SpotifyWebApi.searchForSong(songName)[0]
            }
        }
        songsList.awaitAll()
    }

    fun speechToTextButtonClicked() {
        if (_uiState.value.isListening) {
            stopListening()
        } else {
            startListening(Locale.current.language)
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
