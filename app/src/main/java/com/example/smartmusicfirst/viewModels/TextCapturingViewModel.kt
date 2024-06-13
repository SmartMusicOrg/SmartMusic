package com.example.smartmusicfirst.viewModels

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.TAG
import com.example.smartmusicfirst.connectors.croticalio.CroticalioApi
import com.example.smartmusicfirst.connectors.gemini.GeminiApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.uiStates.TextCapturingUiState
import com.example.smartmusicfirst.models.KeywordCroticalio
import com.example.smartmusicfirst.models.SpotifyPlaylist
import com.example.smartmusicfirst.models.SpotifySong
import com.example.smartmusicfirst.models.SpotifyUser
import com.example.smartmusicfirst.playPlaylist
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    fun searchSong(corticalioAccessToken: String, geminiApiKey: String) {
        viewModelScope.launch {
            try {
                // Take the keywords
                val keywords = getKeywordDeferred(corticalioAccessToken).await()

                // Filter the keywords and get current user preferences

                // Give Gemini the keywords and extract relevant songs
                val response = getGeminiResponseDeferred(geminiApiKey, keywords).await()

                // Clean the response of Gemini
                val songs = getSongsNamesFromGeminiResponse(response)
                Log.d(TAG, "Songs: $songs")

                // Get the songs from Spotify
                val songsList = getSongsList(songs).await()

                // Get the current user
                val user = getCurrentUser().await()

                // Create a playlist that will contain all the songs
                val playlist = createPlaylist(user.id).await()

                // Add the songs to the playlist
                val songUris = songsList.map { it.uri }

                addSongsToPlaylist(playlist.id, songUris)

                // Play the playlist
                playPlaylist(playlist.uri)
                //me and my girlfriend having fun together
            } catch (e: Exception) {
                Log.e(TAG, "Error during API calls", e)
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
        Log.d(TAG, "Keywords template: $keywordsTemplate")
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
        val rows = response.split("\n")
        val songs = mutableListOf<String>()
        for (row in rows) {
            if (row.isNotEmpty()) {
                row.replace("\"", "")
                row.subSequence(2, row.length).toString().let {
                    songs.add(it)
                }
            }
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

    private fun getCurrentUser(): CompletableDeferred<SpotifyUser> {
        val deferred = CompletableDeferred<SpotifyUser>()
        if (SpotifyWebApi.currentUser != null) {
            SpotifyWebApi.getCurrentUserDetails { user ->
                deferred.complete(user)
            }
            return deferred
        }
        SpotifyWebApi.getCurrentUserDetails { user ->
            deferred.complete(user)
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

    private fun addSongsToPlaylist(playlistId: String, songUris: List<String>) {
        viewModelScope.launch {
            try {
                SpotifyWebApi.addItemsToExistingPlaylist(playlistId, songUris)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding songs to playlist", e)
            }
        }
    }

    fun speechToText(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition is not available")
            return
        }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.current.language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.current.language)
            putExtra(
                RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
                Locale.current.language
            )
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Beginning of speech")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "End of speech")
            }

            override fun onError(error: Int) {
                Log.e(TAG, "Error during speech recognition: $error")
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.let { resultList ->
                        if (resultList.isNotEmpty()) {
                            val recognizedText = resultList[0]
                            updateInputString(recognizedText)
                            Log.d(TAG, "Recognized text: $recognizedText")
                        }
                    }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }


}
