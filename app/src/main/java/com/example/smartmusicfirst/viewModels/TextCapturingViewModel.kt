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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.firebase.FirebaseApi
import com.example.smartmusicfirst.connectors.gemini.GeminiApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import com.example.smartmusicfirst.models.KeywordCroticalio
import com.example.smartmusicfirst.models.SpotifyPlaylist
import com.example.smartmusicfirst.models.SpotifySong
import com.example.smartmusicfirst.playPlaylist
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TextCapturingViewModel(application: Application) : AndroidViewModel(application),
    RecognitionListener {
    private val app = application
    private val _uiState = MutableStateFlow(TextCapturingUiState())
    val uiState: StateFlow<TextCapturingUiState> = _uiState.asStateFlow()
    private var recognizer = SpeechRecognizer.createSpeechRecognizer(application)

    fun updateInputString(str: String) {
        _uiState.value = _uiState.value.copy(inputString = str)
    }

    fun enableRecording(isAvailable: Boolean) {
        _uiState.value = _uiState.value.copy(recordingGranted = isAvailable)
    }

    fun searchSong(corticalioAccessToken: String, geminiApiKey: String) {
        _uiState.value = _uiState.value.copy(canUseRecord = false, canUseSubmit = false)
        viewModelScope.launch {
            try {
                val firebaseApi = FirebaseApi()

                // Take the keywords
                val keywords = getKeywordDeferred(corticalioAccessToken).await()

                // Filter the keywords and get current user preferences

                // Give Gemini the keywords and extract relevant songs
                val response = getGeminiResponseDeferred(geminiApiKey, keywords).await()
                Log.d(DEBUG_TAG, "Gemini response: $response")

                if (response.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        canUseRecord = true,
                        canUseSubmit = true,
                        errorMessage = "Can not find songs to play. Please try again."
                    )
                    return@launch
                }

                // Clean the response of Gemini
                val songs = getSongsNamesFromGeminiResponse(response)
                Log.d(DEBUG_TAG, "Songs: $songs")

                // Get the songs from Spotify
                val songsList = getSongsList(songs).await()


                try {
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


                // Create a playlist that will contain all the songs
                val playlist = createPlaylist(SpotifyWebApi.currentUser.id).await()

                // Add the songs to the playlist
                val songUris = songsList.map { it.uri }
                Log.d(DEBUG_TAG, "Song URIs: $songUris")

                SpotifyWebApi.addItemsToExistingPlaylist(playlist.id, songUris).await()
                Log.d(DEBUG_TAG, "Songs added to playlist")

                // Play the playlist
                playPlaylist(playlist.uri)
                //me and my girlfriend having fun together
            } catch (e: Exception) {
                Log.e(DEBUG_TAG, "Error during API calls", e)
                _uiState.value = _uiState.value.copy(
                    canUseRecord = true,
                    canUseSubmit = true,
                    errorMessage = e.message ?: "An error occurred"
                )
            }
        }
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
        Log.d(DEBUG_TAG, "Keywords template: $keywordsTemplate")
        val inputQuery =
            "give me list of top fifteen popular songs that connected to the following words: $keywordsTemplate give only the list without any other world accept the list"

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

    private fun getSongsNamesFromGeminiResponse(response: String): List<String> {
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
            Log.e(DEBUG_TAG, "Error during parsing Gemini response", e)
            updateInputString("Can not find songs to play. Please try again.")
        }
        return songs
    }

    private fun getSongsList(songs: List<String>): CompletableDeferred<List<SpotifySong>> {
        val deferred = CompletableDeferred<List<SpotifySong>>()
        val songsList = mutableListOf<SpotifySong>()

        viewModelScope.launch {
            try {
                val songDeferreds = songs.map { song ->
                    async {
                        val songDeferred = CompletableDeferred<SpotifySong>()
                        SpotifyWebApi.searchForSong(song) { searchResults ->
                            if (searchResults.isNotEmpty()) {
                                songDeferred.complete(searchResults[0])
                            } else {
                                songDeferred.completeExceptionally(Exception("No songs found for: $song"))
                            }
                        }
                        songDeferred.await()
                    }
                }

                val results = songDeferreds.awaitAll()
                songsList.addAll(results)
                deferred.complete(songsList)
            } catch (e: Exception) {
                deferred.completeExceptionally(e)
            }
        }

        return deferred
    }

    private fun createPlaylist(userId: String): CompletableDeferred<SpotifyPlaylist> {
        val deferred = CompletableDeferred<SpotifyPlaylist>()
        val playlistName = "Smart Music First Playlist"
        SpotifyWebApi.createPlaylist(userId, playlistName) { playlist ->
            if (playlist != null) {
                deferred.complete(playlist)
            }
        }
        return deferred
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
