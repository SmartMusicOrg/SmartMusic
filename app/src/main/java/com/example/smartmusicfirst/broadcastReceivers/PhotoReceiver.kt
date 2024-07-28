package com.example.smartmusicfirst.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.example.smartmusicfirst.services.NotificationCenter
import java.io.IOException
import java.util.concurrent.Executors

class PhotoReceiver : BroadcastReceiver() {
    private var contentObserver: ContentObserver? = null
    private val executor = Executors.newSingleThreadExecutor()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MEDIA_SCANNER_FINISHED) {
//            registerContentObserver(context)
        }
    }

    fun registerContentObserver(context: Context) {
        if (contentObserver == null) {
            val handler = Handler(Looper.getMainLooper())
            contentObserver = object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    super.onChange(selfChange, uri)

                    uri?.let {
                        executor.execute {
                            val bitmap = getBitmapFromUri(context, it)
                            if (bitmap != null) {
                                NotificationCenter.createNotification(
                                    context = context,
                                    title = "New Photo Detected",
                                    message = "A new photo has been added to your gallery.",
                                    notificationId = 1,
                                    bitmap = bitmap,
                                    image = it
                                )
                            }
                        }
                    }
                }
            }

            context.contentResolver.registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver!!
            )
        }
    }

    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun unregisterContentObserver(context: Context) {
        contentObserver?.let {
            context.contentResolver.unregisterContentObserver(it)
            contentObserver = null
        }
    }
}
