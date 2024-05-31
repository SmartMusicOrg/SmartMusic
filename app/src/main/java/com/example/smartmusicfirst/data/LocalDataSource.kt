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
                icon = R.drawable.home_icon,
                hasNew = false,
                iconTitle = R.string.homePage_title_icon
            ),
            NavigationItem(
                route = Routs.Notifications.name,
                title = Routs.Notifications.title,
                icon = R.drawable.notifications_icon,
                hasNew = false,
                iconTitle = R.string.NotificationsPage_title_icon
            ),
            NavigationItem(
                route = Routs.Settings.name,
                title = Routs.Settings.title,
                icon = R.drawable.settings_icon,
                hasNew = false,
                iconTitle = R.string.SettingsPage_title_icon
            ),
            NavigationItem(
                route = Routs.ContactUs.name,
                title = Routs.ContactUs.title,
                icon = R.drawable.mail_icon,
                hasNew = false,
                iconTitle = R.string.ContactUsPage_title_icon
            )
        )
    }
}