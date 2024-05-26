package com.example.smartmusicfirst.models

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.smartmusicfirst.data.Routs

data class NavigationItem(
    val route: String,
    @StringRes val title: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNew: Boolean,
    val badgeCount: Int? = null
) {
    fun bottomNavigationItems(): List<NavigationItem> {
        return listOf(
            NavigationItem(
                route = Routs.HomePage.name,
                title = Routs.HomePage.title,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNew = false
            ),
            NavigationItem(
                route = Routs.MusicPlaysPage.name,
                title = Routs.MusicPlaysPage.title,
                selectedIcon = Icons.Filled.Menu,
                unselectedIcon = Icons.Outlined.Menu,
                hasNew = false
            ),
            NavigationItem(
                route = Routs.Notifications.name,
                title = Routs.Notifications.title,
                selectedIcon = Icons.Filled.Call,
                unselectedIcon = Icons.Outlined.Call,
                hasNew = false
            ),
            NavigationItem(
                route = Routs.Settings.name,
                title = Routs.Settings.title,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                hasNew = false
            ),
            NavigationItem(
                route = Routs.ContactUs.name,
                title = Routs.ContactUs.title,
                selectedIcon = Icons.Filled.Email,
                unselectedIcon = Icons.Outlined.Email,
                hasNew = false
            )
        )
    }
}
