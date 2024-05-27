package com.example.smartmusicfirst.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smartmusicfirst.data.Routs
import com.example.smartmusicfirst.ui.components.BottomNavigation
import com.example.smartmusicfirst.ui.components.NavigationMenu
import com.example.smartmusicfirst.ui.components.SmartMusicTopBar

@Composable
fun SmartMusicScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Routs.valueOf(backStackEntry?.destination?.route ?: Routs.HomePage.name)

    Scaffold(
        topBar = {
            SmartMusicTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() })
        },
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavigationMenu(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}