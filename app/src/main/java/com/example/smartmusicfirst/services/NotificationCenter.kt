package com.example.smartmusicfirst.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME_CAMERA,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(context: Context, title: String, message: String, notificationId: Int) {
        val notification =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo_app)
                    .setAutoCancel(true)
                    .build()
            } else {
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo_app)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()
            }
        notificationManager.notify(notificationId, notification)
    }

    fun createNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int,
        bitmap: Bitmap
    ) {
        val notification =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setStyle(Notification.BigPictureStyle().bigPicture(bitmap))
                    .setSmallIcon(R.drawable.logo_app)
                    .build()
            } else {
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setSmallIcon(R.drawable.logo_app)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
            }
        notificationManager.notify(notificationId, notification)
    }
}
