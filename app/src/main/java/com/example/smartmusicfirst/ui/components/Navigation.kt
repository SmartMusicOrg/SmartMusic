package com.example.smartmusicfirst.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartmusicfirst.ImageNotificationUri
import com.example.smartmusicfirst.data.Routs
import com.example.smartmusicfirst.ui.views.ContactUsScreen
import com.example.smartmusicfirst.ui.views.HomePageScreen
import com.example.smartmusicfirst.ui.views.ImageCapturingScreen
import com.example.smartmusicfirst.ui.views.MySimpleAppContainer
import com.example.smartmusicfirst.ui.views.PlaceHolderScreen
import com.example.smartmusicfirst.ui.views.PlayerPageScreen
import com.example.smartmusicfirst.ui.views.SettingsScreen
import com.example.smartmusicfirst.ui.views.TextCapturingScreen

@Composable
fun NavigationMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Routs.valueOf(backStackEntry?.destination?.route ?: Routs.HomePage.name)
    NavHost(
        navController = navController,
        startDestination = Routs.HomePage.name,
        modifier = modifier
    ) {

        composable(Routs.HomePage.name) {
            HomePageScreen(
                onNavigateToEmotionButtons = { navController.navigate(Routs.EmotionsButtons.name) },
                onNavigateToTextCapturing = { navController.navigate(Routs.TextCapturing.name) },
                onNavigateToImageCapturing = { navController.navigate(Routs.ImageCapturing.name) },
                modifier = Modifier.fillMaxSize()
            )
            if (ImageNotificationUri != null) {
                navController.navigate(Routs.ImageCapturing.name)
            }
        }

        composable(Routs.TextCapturing.name) {
            TextCapturingScreen(
                onNavigateToPlayerPage = { navController.navigate(Routs.PlayerPage.name) },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.ImageCapturing.name) {
            ImageCapturingScreen(
                onNavigateToPlayerPage = { navController.navigate(Routs.PlayerPage.name) },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.PlayerPage.name) {
            PlayerPageScreen(modifier = Modifier.fillMaxSize())
        }

        composable(Routs.Notifications.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.Settings.name) {
            SettingsScreen(
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.ContactUs.name) {
            ContactUsScreen(
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.EmotionsButtons.name) {
            MySimpleAppContainer(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                onNavigateToPlayerPage = { navController.navigate(Routs.PlayerPage.name) }
            )
        }

    }
}
