package com.example.smartmusicfirst.viewModels

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.connectors.ai.AIApi
import com.example.smartmusicfirst.connectors.ai.GeminiApi
import com.example.smartmusicfirst.connectors.datastore.DataStorePreferences
import com.example.smartmusicfirst.connectors.firebase.FirebaseApi
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.LoadingHintsEnum
import com.example.smartmusicfirst.data.uiStates.ImageCapturingUiState
import com.example.smartmusicfirst.playPlaylist
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.system.measureTimeMillis

class ImageCapturingViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val _uiState = MutableStateFlow(ImageCapturingUiState())
    val uiState = _uiState.asStateFlow()


    fun setImageUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(imageUri = uri)
    }

    fun loadImage(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeStream(app.contentResolver.openInputStream(uri))
                _uiState.value = _uiState.value.copy(imageBitmap = bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun searchSong(
        aiApiKey: String,
        aiModel: AIApi = GeminiApi,
        onNavigateToPlayerPage: () -> Unit
    ) {
        _uiState.value =
            _uiState.value.copy(canUseSubmit = false, isLoading = true)
        viewModelScope.launch {
            val time = measureTimeMillis {
                try {
                    if (_uiState.value.imageBitmap == null) {
                        _uiState.value = _uiState.value.copy(
                            canUseSubmit = true,
                            toastMessage = "No image selected",
                            isLoading = false
                        )
                        return@launch
                    }
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    val image = InputImage.fromBitmap(_uiState.value.imageBitmap!!, 0)
                    val labels = mutableListOf<String>()
                    val imageLabelingJob = withContext(Dispatchers.IO) {
                        labeler.process(image).addOnSuccessListener { labelsArray ->
                            labelsArray.map {
                                Log.d(DEBUG_TAG, "Label: ${it.text}, Confidence: ${it.confidence}")
                                if (it.confidence > 0.7) {
                                    labels.add(it.text)
                                }
                            }
                        }.addOnFailureListener {
                            Log.e(DEBUG_TAG, "Error during image labeling", it)
                            emptyList<String>()
                        }
                    }
                    imageLabelingJob.await()
                    if (labels.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            canUseSubmit = true,
                            toastMessage = "No labels found",
                            isLoading = false
                        )
                        return@launch
                    }
                    Log.d(DEBUG_TAG, "after filtering labels:")
                    for (label in labels) {
                        Log.d(DEBUG_TAG, "Label: $label")
                    }

                    _uiState.value =
                        _uiState.value.copy(userHint = LoadingHintsEnum.GET_AI_OFFER.hintState)
                    // Give AI the keywords and extract relevant songs
                    val query = aiModel.buildQuery(labels)

                    val response = aiModel.getResponse(query, aiApiKey)
                    Log.d(DEBUG_TAG, "Ai response: $response")

                    if (response.isEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            canUseSubmit = true,
                            toastMessage = "Can not find songs to play. Please try again."
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
                            val str = firebaseApi.uploadImage(_uiState.value.imageUri!!)
                            Log.d(DEBUG_TAG, "Image uploaded to Firebase: $str")
                            val queryDocRef = withContext(Dispatchers.IO) {
                                firebaseApi.addDoc(
                                    "users/${SpotifyWebApi.currentUser.id}/queries",
                                    mapOf(
                                        "isImage" to true,
                                        "imageUri" to str,
                                        "keywords" to labels.joinToString(", "),
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
                        canUseSubmit = true,
                        isLoading = false
                    )


                } catch (e: Exception) {
                    Log.e(DEBUG_TAG, "Error during API calls", e)
                    _uiState.value = _uiState.value.copy(
                        canUseSubmit = true,
                        toastMessage = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
            Log.d(DEBUG_TAG, "Time taken: $time ms")
            onNavigateToPlayerPage()
        }
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }
}
