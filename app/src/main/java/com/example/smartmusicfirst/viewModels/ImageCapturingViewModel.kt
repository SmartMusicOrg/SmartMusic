package com.example.smartmusicfirst.viewModels

import android.app.Application
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmusicfirst.DEBUG_TAG
import com.example.smartmusicfirst.data.uiStates.ImageCapturingUiState
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

    fun searchSong() {
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
        }
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }
}
