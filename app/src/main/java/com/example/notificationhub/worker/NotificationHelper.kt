package com.example.notificationhub.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.util.AppConstant
import java.util.concurrent.TimeUnit

/**
 * Helper object for managing notification channels,
 * scheduling and cancelling notifications using WorkManager,
 * and sending immediate or test notifications.
 */
object NotificationHelper {

    /**
     * Creates the notification channel required on Android O and above.
     *
     * @param context Application context for creating the channel
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.NOTIFICATION_CHANNEL_ID,
                AppConstant.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications from Notification Hub"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Sends a test notification immediately.
     * Requires POST_NOTIFICATIONS permission on Android 13+.
     *
     * @param context Application context
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendTestNotificationImmediate(context: Context) {
        showNotification(
            context = context,
            title = "Test Notification",
            message = "This is a test notification from Notification Hub!",
            deepLink = "notifications",
            notificationType = "Test Notification"
        )
    }

    /**
     * Shows a notification immediately using NotificationManagerCompat.
     *
     * @param context Application context
     * @param title Notification title
     * @param message Notification message
     * @param deepLink Deep link URI string to open on notification tap
     * @param notificationType Notification type for analytics (defaults to title)
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        deepLink: String,
        notificationType: String = title
    ) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("${AppConstant.DEEP_LINK_SCHEME}://$deepLink?type=${Uri.encode(notificationType)}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                title.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, AppConstant.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
