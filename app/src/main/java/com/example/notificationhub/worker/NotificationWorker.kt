package com.example.notificationhub.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.util.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val title = inputData.getString("title") ?: "Notification Hub"
            val message = inputData.getString("message") ?: "You have a new notification"
            val deepLink = inputData.getString("deepLink") ?: "home"

            showNotification(title, message, deepLink)

            trackNotificationSent(title)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String, deepLink: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("${AppConstant.DEEP_LINK_SCHEME}://$deepLink?type=${Uri.encode(title)}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                title.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(
                applicationContext,
                AppConstant.NOTIFICATION_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext)
                .notify(title.hashCode(), notification)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun trackNotificationSent(notificationType: String) {
        withContext(Dispatchers.IO) {
            try {
                val dao = AppDatabase.getDatabase(applicationContext).analyticsDao()
                dao.insertAnalytic(
                    AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = "sent"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
