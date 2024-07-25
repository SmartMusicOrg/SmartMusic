package com.example.smartmusicfirst.ui.views.homepage

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.extensions.testInit
import com.example.smartmusicfirst.models.SpotifyUser
import com.example.smartmusicfirst.ui.views.HomePageScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomePageScreenKtTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()


    @Before
    fun setup() {
        composeTestRule.setContent {
            val user = SpotifyUser("test", "test", "test", listOf(), "premium")
            SpotifyWebApi.testInit(LocalContext.current.applicationContext, user)
            HomePageScreen(
                onNavigateToEmotionButtons = { },
                onNavigateToTextCapturing = { },
                onNavigateToImageCapturing = { }
            )
        }
    }

    @Test
    fun userNameIsDisplayed() {
        composeTestRule.onNodeWithText("Hello, test!").assertExists()
    }
}