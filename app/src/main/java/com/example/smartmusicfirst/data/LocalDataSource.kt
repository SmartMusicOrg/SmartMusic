package com.example.smartmusicfirst.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.models.NavigationItem

object LocalDataSource {
    fun getNavigationItems(): List<NavigationItem> {
        return listOf(
//      todo remove:
//            NavigationItem(
////                route = Routs.MusicPlaysPage.name,
//                route = Routs.EmotionsButtons.name,
//                title = Routs.MusicPlaysPage.title,
//                selectedIcon = Icons.Filled.Menu,
//                unselectedIcon = Icons.Outlined.Menu,
//                hasNew = false,
//                iconTitle = R.string.musicPlaysPage_title_icon
//            ),
            NavigationItem(
                route = Routs.HomePage.name,
                title = Routs.HomePage.title,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                hasNew = false,
                iconTitle = R.string.homePage_title_icon
            ),
            NavigationItem(
                route = Routs.Notifications.name,
                title = Routs.Notifications.title,
                selectedIcon = Icons.Filled.Call,
                unselectedIcon = Icons.Outlined.Call,
                hasNew = false,
                iconTitle = R.string.NotificationsPage_title_icon
            ),
            NavigationItem(
                route = Routs.ContactUs.name,
                title = Routs.ContactUs.title,
                selectedIcon = Icons.Filled.Email,
                unselectedIcon = Icons.Outlined.Email,
                hasNew = false,
                iconTitle = R.string.ContactUsPage_title_icon
            ),
            NavigationItem(
                route = Routs.Settings.name,
                title = Routs.Settings.title,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                hasNew = false,
                iconTitle = R.string.SettingsPage_title_icon
            )
        )
    }
}