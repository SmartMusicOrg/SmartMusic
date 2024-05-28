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
import com.example.smartmusicfirst.data.Routs
import com.example.smartmusicfirst.ui.views.HomePageScreen
import com.example.smartmusicfirst.ui.views.MySimpleAppContainer
import com.example.smartmusicfirst.ui.views.PlaceHolderScreen

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
                onNavigateToTextCapturing = { navController.navigate(Routs.ImageCapturing.name) },
                onNavigateToImageCapturing = { navController.navigate(Routs.TextCapturing.name) },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Routs.MusicPlaysPage.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.TextCapturing.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.ImageCapturing.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.PlayerPage.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.Notifications.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.Settings.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.ContactUs.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxSize()
            )
        }

        composable(Routs.EmotionsButtons.name) {
            MySimpleAppContainer(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentSize()
            )
        }

    }
}
