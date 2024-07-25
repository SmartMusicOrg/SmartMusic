package com.example.smartmusicfirst.ui.views

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.connectors.spotify.SpotifyWebApi
import com.example.smartmusicfirst.data.Routs
import com.example.smartmusicfirst.extensions.assertCurrentRouteName
import com.example.smartmusicfirst.extensions.onNodeWithStringId
import com.example.smartmusicfirst.extensions.testInit
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationKtTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        composeTestRule.setContent {
            SpotifyWebApi.testInit(LocalContext.current.applicationContext)
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            SmartMusicScreen(
                navController = navController
            )
        }
    }

    @Test
    fun verifyStartDestination() {
        navController.assertCurrentRouteName(Routs.HomePage.name)
    }

    @Test
    fun testNavigation_toImageCapturing() {
        composeTestRule.onNodeWithStringId(R.string.imageCapturingPage_title)
            .performClick()
        navController.assertCurrentRouteName(Routs.ImageCapturing.name)
    }

    @Test
    fun testNavigation_toTextCapturing() {
        composeTestRule.onNodeWithStringId(R.string.textCapturingPage_title)
            .performClick()
        navController.assertCurrentRouteName(Routs.TextCapturing.name)
    }

    @Test
    fun testNavigation_toEmotionsButtons() {
        composeTestRule.onNodeWithStringId(R.string.emotionButtonsPage_title)
            .performClick()
        navController.assertCurrentRouteName(Routs.EmotionsButtons.name)
    }

    @Test
    fun testNavigation_toContactUs() {
        composeTestRule.onNodeWithStringId(R.string.ContactUsPage_title)
            .performClick()
        navController.assertCurrentRouteName(Routs.ContactUs.name)
    }
}
