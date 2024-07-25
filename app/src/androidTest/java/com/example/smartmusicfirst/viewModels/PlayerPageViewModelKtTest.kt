package com.example.smartmusicfirst.viewModels


import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.extensions.testInit
import com.example.smartmusicfirst.models.SpotifyUser
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerPageViewModelKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var playerPageViewModel: PlayerPageViewModel

    @Before
    fun setUp() {
        composeTestRule.setContent {
            val user = SpotifyUser("test", "test", "test", listOf(), "premium")
            SpotifyWebApi.testInit(LocalContext.current.applicationContext, user)
            playerPageViewModel =
                PlayerPageViewModel()
        }
    }

    @Test
    fun initialIsPlayingMode() {
        assertThat(playerPageViewModel.uiState.value.isPlaying).isTrue()
    }
}