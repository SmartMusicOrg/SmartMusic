package com.example.smartmusicfirst.ui.views.playerpage

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.extensions.testInit
import com.example.smartmusicfirst.models.SpotifyUser
import com.example.smartmusicfirst.ui.views.PlayerPageScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayerPageScreenKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    @Before
    fun setup() {
        composeTestRule.setContent {
            val user = SpotifyUser("test", "test", "test", listOf(), "premium")
            SpotifyWebApi.testInit(LocalContext.current.applicationContext, user)
            PlayerPageScreen()
        }
    }

    @Test
    fun allButtonsEnabled_PremiumUser() {
        val playbackButtons =
            composeTestRule.onAllNodesWithContentDescription("playback control button")
        val nodes = playbackButtons.fetchSemanticsNodes(atLeastOneRootRequired = false)
        for (i in 0 until nodes.size) {
            playbackButtons.get(i).assertIsEnabled()
        }
    }
}

class TestForFreeUser {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        composeTestRule.setContent {
            val user = SpotifyUser("test", "test", "test", listOf(), "free")
            SpotifyWebApi.testInit(LocalContext.current.applicationContext, user)
            PlayerPageScreen()
        }
    }

    @Test
    fun onlyPauseEnable_FreeUser() {
        val playbackButtons =
            composeTestRule.onAllNodesWithContentDescription("playback control button")
        val nodes = playbackButtons.fetchSemanticsNodes(atLeastOneRootRequired = false)
        for (i in 0 until nodes.size) {
            if (i == 1) {
                playbackButtons.get(i).assertIsEnabled()
            } else {
                playbackButtons.get(i).assertIsNotEnabled()
            }
        }

    }
}