package com.example.smartmusicfirst.viewModels


import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.smartmusicfirst.MyApplication
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ImageCapturingViewModelKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var imageCapturingViewModel: ImageCapturingViewModel

    @Before
    fun setUp() {
        composeTestRule.setContent {
            imageCapturingViewModel =
                ImageCapturingViewModel(MyApplication())
        }
    }

    @Test
    fun setImageUri() {
        val imageUri = Uri.parse("uri")
        imageCapturingViewModel.setImageUri(imageUri)
        assertThat(imageCapturingViewModel.uiState.value.imageUri).isEqualTo(imageUri)
    }

    @Test
    fun showToast() {
        imageCapturingViewModel.showToast("hello")
        assertThat(imageCapturingViewModel.uiState.value.toastMessage).isEqualTo("hello")
    }

}