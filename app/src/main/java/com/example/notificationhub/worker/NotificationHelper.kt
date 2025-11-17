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

object NotificationHelper {

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

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(context: Context, config: NotificationConfig) {
        val data = workDataOf(
            "id" to config.id,
            "title" to config.type,
            "message" to config.message,
            "deepLink" to config.deepLink
        )

        val repeatInterval = when (config.repeatInterval.lowercase()) {
            "daily", "every day" -> 24L
            "weekly" -> 168L
            "twice a week" -> 84L
            else -> 24L
        }

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatInterval, TimeUnit.HOURS,
            15, TimeUnit.MINUTES
        )
            .setInputData(data)
            .addTag(config.id)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                config.id,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }

    fun cancelNotification(context: Context, id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(id)
    }

    fun sendTestNotification(context: Context, title: String, message: String, deepLink: String = "home") {
        val data = workDataOf(
            "title" to title,
            "message" to message,
            "deepLink" to deepLink
        )

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendTestNotificationImmediate(context: Context) {
        showNotification(
            context = context,
            title = "Test Notification",
            message = "This is a test notification from Notification Hub!",
            deepLink = "notifications",  // Changed from "home"
            notificationType = "Test Notification"
        )
    }

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
