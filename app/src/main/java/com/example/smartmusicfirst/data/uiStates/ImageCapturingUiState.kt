package com.example.smartmusicfirst.data.uiStates

import android.graphics.Bitmap
import android.net.Uri
import com.example.smartmusicfirst.data.LoadingHintsEnum

data class ImageCapturingUiState(
    val imageUri: Uri? = null,
    val imageBitmap: Bitmap? = null,
    val isImageCaptured: Boolean = false,
    val canUseSubmit: Boolean = true,
    val toastMessage: String = "",
    val isLoading: Boolean = false,
    val userHint: Int = LoadingHintsEnum.START.hintState
)
