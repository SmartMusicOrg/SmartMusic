package com.example.smartmusicfirst.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartmusicfirst.data.Routs
import com.example.smartmusicfirst.ui.PlaceHolder.PlaceHolderScreen

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
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.MusicPlaysPage.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.EmotionsButtons.name) {}

        composable(Routs.TextCapturing.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.ImageCapturing.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.PlayerPage.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.Notifications.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.Settings.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

        composable(Routs.ContactUs.name) {
            PlaceHolderScreen(
                title = "Missing",
                message = "This page is not implemented yet.",
                modifier = modifier.fillMaxHeight()
            )
        }

    }
}
