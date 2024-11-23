package com.ih.osm.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ih.osm.R
import com.ih.osm.ui.utils.EMPTY
import javax.inject.Inject
import kotlin.random.Random

class NotificationManager
@Inject
constructor(private val context: Context) {
    private fun getChannelId(): String = "${Random.nextInt(1000, 9000)}"

    private fun getNotificationId(): Int = Random.nextInt(100, 900)

    private val maxProgress = 100

    fun buildNotification(title: String = EMPTY, description: String = EMPTY) {
        try {
            val builder = getBuilderNotification(title, description, icon = R.drawable.ic_check)

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

    fun buildNotificationSuccessCard(cardId: String) {
        try {
            buildNotification(
                title = "${context.getString(R.string.card_successfully)} $cardId",
                description = context.getString(R.string.card_successfully_desc)
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
        }
    }

    fun buildNotificationSuccessChangePassword() {
        try {
            buildNotification(
                title = context.getString(R.string.password_reset),
                description = context.getString(R.string.password_successfully_updated)
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log(e.localizedMessage.orEmpty())
        }
    }

    fun buildProgressNotification(): Int {
        val currentProgress = 0
        val notificationId = getNotificationId()
        val builder =
            getBuilderNotification(
                context.getString(R.string.uploading_cards),
                context.getString(R.string.we_re_uploading_the_local_cards),
                R.drawable.ic_cloud
            )

        NotificationManagerCompat.from(context).apply {
            builder.setProgress(maxProgress, currentProgress, false)
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

    fun updateNotificationProgress(notificationId: Int, currentProgress: Int) {
        val description =
            if (currentProgress == 100) {
                context.getString(R.string.cards_uploaded_successfully)
            } else {
                context.getString(R.string.we_re_uploading_the_local_cards)
            }

        val title =
            if (currentProgress == 100) {
                context.getString(R.string.success)
            } else {
                context.getString(R.string.in_progress)
            }

        val builder =
            getBuilderNotification(title, description, R.drawable.ic_cloud)
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
                    builder.setProgress(maxProgress, currentProgress, false)
                }
                notify(notificationId, builder.build())
            }
        }
    }

    fun getBuilderNotification(
        title: String,
        description: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH,
        icon: Int = R.drawable.ic_circle_notification
    ): NotificationCompat.Builder {
        val channelId = getChannelId()
        buildNotificationChannel(channelId)
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(icon)
            .setPriority(priority)
            .setShowWhen(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    private fun buildNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "osm_create_card"
            val descriptionText = "Create card notification"

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(channelId, name, importance).apply {
                    description = descriptionText
                }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildErrorNotification(message: String) {
        buildNotification(
            context.getString(R.string.something_went_wrong),
            context.getString(
                R.string.error,
                message
            ),
        )
    }
}
