package com.ih.m2.core.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.m2.R
import com.ih.m2.ui.utils.EMPTY
import javax.inject.Inject
import kotlin.random.Random

class NotificationManager @Inject constructor(private val context: Context) {
    private fun getChannelId(): String = "${Random.nextInt(1000, 9000)}"
    private fun getNotificationId(): Int = Random.nextInt(100, 900)


    fun buildNotification(
        title: String = EMPTY,
        description: String = EMPTY
    ) {
        try {
            val builder = getBuilderNotification(title, description)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notify(getNotificationId(), builder.build())
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
        }
    }

    fun buildProgressNotification(
        updateProgress: Boolean = false,
    ): Int {
        val currentProgress = 0
        val PROGRESS_MAX = 100
        val notificationId = getNotificationId()
        val builder =
            getBuilderNotification("Uploading cards", "We're uploading the local cards...")

        NotificationManagerCompat.from(context).apply {
            builder.setProgress(PROGRESS_MAX, currentProgress, false)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
            }

        }
        return notificationId
    }

    fun updateNotificationProgress(
        notificationId: Int,
        currentProgress: Int
    ) {
        val PROGRESS_MAX = 100

        val description = if (currentProgress == 100) {
            "Cards uploaded successfully"
        } else {
            "We're uploading the local cards..."
        }

        val title = if (currentProgress == 100) {
            "Success!"
        } else {
            "Uploading cards"
        }

        val builder =
            getBuilderNotification(title, description)
        NotificationManagerCompat.from(context).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                builder.setSilent(true)
                if (currentProgress == 100) {
                    builder.setProgress(0, 0, false)
                } else {
                    builder.setProgress(PROGRESS_MAX, currentProgress, false)
                }
                notify(notificationId, builder.build())
            }
        }
    }

    private fun getBuilderNotification(
        title: String,
        description: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ): NotificationCompat.Builder {
        val channelId = getChannelId()
        buildNotificationChannel(channelId)
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_circle_notification)
            .setPriority(priority)
            .setAutoCancel(true)
    }


    private fun buildNotificationChannel(
        channelId: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "m2_create_card"
            val descriptionText = "Create card notification"

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildErrorNotification(
        notificationId: Int,
        message: String
    ) {
        val builder =
            getBuilderNotification("Ups!", "Error: $message")
        NotificationManagerCompat.from(context).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                builder.setSilent(true)
                builder.setProgress(0, 0, false)
                notify(notificationId, builder.build())
            }
        }
    }

}