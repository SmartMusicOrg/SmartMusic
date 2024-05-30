package com.example.smartmusicfirst.data

import androidx.annotation.StringRes
import com.example.smartmusicfirst.R

enum class Routs(@StringRes val title: Int) {
    HomePage(title = R.string.homePage_title),
    EmotionsButtons(title = R.string.emotionButtonsPage_title),
    TextCapturing(title = R.string.textCapturingPage_title),
    ImageCapturing(title = R.string.imageCapturingPage_title),
    PlayerPage(title = R.string.PlayerPage_title),
    Notifications(title = R.string.NotificationsPage_title),
    Settings(title = R.string.SettingsPage_title),
    ContactUs(title = R.string.ContactUsPage_title),
    PlaceHolder(title = R.string.PlaceHolderPage_title)
}