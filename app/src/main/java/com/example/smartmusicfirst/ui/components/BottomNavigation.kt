package com.example.smartmusicfirst.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.smartmusicfirst.data.LocalDataSource
import com.example.smartmusicfirst.data.Routs

@Composable
fun BottomNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Routs.valueOf(backStackEntry?.destination?.route ?: Routs.HomePage.name)
    val navigationItemsList = LocalDataSource.getNavigationItems()

    NavigationBar {
        navigationItemsList.forEach { item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = rememberVectorPainter(image = item.selectedIcon),
                        contentDescription = null
                    )
                },
                label = {
                    if (item.iconTitle != null && currentScreen.name == item.route) {
                        Text(
                            text = stringResource(id = item.iconTitle),
                            modifier = Modifier
                        )
                    }
                },
                selected = currentScreen.name == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            // we want the application to return to homepage when we click it even if we navigate inside it
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}