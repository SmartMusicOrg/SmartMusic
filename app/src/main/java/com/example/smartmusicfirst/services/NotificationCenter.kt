package com.example.smartmusicfirst.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.smartmusicfirst.MainActivity
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
        bitmap: Bitmap,
        image: Uri
    ) {
        val intent = Intent(context.applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("image", image.toString())
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setStyle(Notification.BigPictureStyle().bigPicture(bitmap))
                    .setSmallIcon(R.drawable.logo_app)
                    .setContentIntent(pendingIntent)
                    .build()
            } else {
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setSmallIcon(R.drawable.logo_app)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .build()
            }
        notificationManager.notify(notificationId, notification)
    }
}
