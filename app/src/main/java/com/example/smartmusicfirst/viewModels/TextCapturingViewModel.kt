package com.example.smartmusicfirst.viewModels

import android.util.Log
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
                // take the key words
                val keywords = getKeywordDeferred(corticalioAccessToken).await()

                // give gemini the key words and extract relevant songs
                val response = getGeminiResponseDeferred(geminiApiKey, keywords).await()

                // clean the response of gemini
                val songs = getSongsNamesFromGeminiResponse(response)
                Log.d(TAG, "Gemini Response: $songs")

                // get the songs from spotify
                val songsList = getSongsList(songs).await()
                Log.d(TAG, "Songs List: $songsList")


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
        if (SpotifyWebApi.currentUser != null) {
            return CompletableDeferred(SpotifyWebApi.currentUser!!)
        }
        val deferred = CompletableDeferred<SpotifyUser>()
        SpotifyWebApi.getCurrentUserDetails { user ->
            deferred.complete(user)
        }
        return deferred
    }

    private fun createPlaylist(): CompletableDeferred<SpotifyPlaylist> {
        val deferred = CompletableDeferred<SpotifyPlaylist>()
        val playlistName = "Smart Music First Playlist"
        val userId = "finalprojectmanager"
        SpotifyWebApi.createPlaylist(userId, playlistName) { playlist ->
            deferred.complete(playlist)
        }
        return deferred
    }


}
