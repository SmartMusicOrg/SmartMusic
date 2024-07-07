package com.example.smartmusicfirst.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import com.example.smartmusicfirst.R

object NotificationCenter {
    private const val CHANNEL_ID = "smart_music_first_channel"
    private const val CHANNEL_NAME_CAMERA = "CAMERA_NOTIFICATION_CHANNEL"
    private const val CHANNEL_DESCRIPTION = "Smart Music First Notifications"
    private lateinit var notificationManager: NotificationManager

    fun init(context: Context) {
        notificationManager =
            getSystemService(context, NotificationManager::class.java) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME_CAMERA,
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .apply {
                description = CHANNEL_DESCRIPTION
            }
        notificationManager.createNotificationChannel(channel)
    }

    fun createNotification(context: Context, title: String, message: String, notificationId: Int) {
        val notification = android.app.Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo_app)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }
}