package com.example.smartmusicfirst.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import com.example.smartmusicfirst.R
import com.example.smartmusicfirst.models.NavigationItem

object LocalDataSource {
    fun getNavigationItems(): List<NavigationItem> {
        return listOf(
            NavigationItem(
                route = Routs.HomePage.name,
                title = Routs.HomePage.title,
                selectedIcon = Icons.Filled.Home,
                hasNew = false,
                iconTitle = R.string.homePage_title_icon
            ),
            NavigationItem(
                route = Routs.Notifications.name,
                title = Routs.Notifications.title,
                selectedIcon = Icons.Filled.Notifications,
                hasNew = false,
                iconTitle = R.string.NotificationsPage_title_icon
            ),
            NavigationItem(
                route = Routs.Settings.name,
                title = Routs.Settings.title,
                selectedIcon = Icons.Filled.Settings,
                hasNew = false,
                iconTitle = R.string.SettingsPage_title_icon
            ),
            NavigationItem(
                route = Routs.ContactUs.name,
                title = Routs.ContactUs.title,
                selectedIcon = Icons.Filled.Email,
                hasNew = false,
                iconTitle = R.string.ContactUsPage_title_icon
            )
        )
    }
}