package com.example.smartmusicfirst.ui.views.textcapturingpage

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.rule.GrantPermissionRule
import com.example.smartmusicfirst.MyApplication
import com.example.smartmusicfirst.ui.views.TextCapturingScreen
import com.example.smartmusicfirst.viewModels.TextCapturingViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TextCapturingPageScreenKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    var permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)


    private lateinit var viewModel: TextCapturingViewModel


    @Before
    fun setup() {
        composeTestRule.setContent {
            val application = MyApplication()
            viewModel = TextCapturingViewModel(application)
            TextCapturingScreen(
                onNavigateToPlayerPage = { },
                textCapturingViewModel = viewModel
            )
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun initialStatus() {
        val uiState = viewModel.uiState.value
        assertThat(uiState.recordingGranted).isTrue()
        assertThat(uiState.errorMessage).isEmpty()
        assertThat(uiState.isLoading).isFalse()
        assertThat(uiState.isListening).isFalse()
        assertThat(uiState.inputString).isEmpty()
        assertThat(uiState.canUseRecord).isTrue()
        assertThat(uiState.canUseSubmit).isTrue()
    }

    @Test
    fun updatesInputString() {
        viewModel.updateInputString("Hello")
        val uiState = viewModel.uiState.value
        assertThat(uiState.inputString).isEqualTo("Hello")
    }

    @Test
    fun enableRecording() {
        viewModel.enableRecording(true)
        val uiState = viewModel.uiState.value
        assertThat(uiState.recordingGranted).isTrue()
    }

    @Test
    fun recordAvailable_recordIsGranted() {
        viewModel.enableRecording(true)
        val uiState = viewModel.uiState.value
        assertThat(uiState.canUseRecord).isTrue()
        composeTestRule.onNodeWithContentDescription("Mic").assertIsEnabled()
    }

    @Test
    fun recordAvailable_recordIsNotGranted() {
        viewModel.enableRecording(false)
        val uiState = viewModel.uiState.value
        assertThat(uiState.recordingGranted).isFalse()
        composeTestRule.onNodeWithContentDescription("Mic").assertIsNotEnabled()
    }

    @Test
    fun submit_inBeginning_isEnabled() {
        composeTestRule.onNodeWithText("Search").assertIsEnabled()
    }

    @Test
    fun submit_WhenCanUseSubmitIsFalse_isDisabled() {
        viewModel.updateCanUseSubmit(false)
        composeTestRule.onNodeWithText("Search").assertIsNotEnabled()
    }
}