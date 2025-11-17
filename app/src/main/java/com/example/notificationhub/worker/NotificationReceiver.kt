package com.example.notificationhub.worker

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.util.AppConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    private val TAG = "NotificationReceiver"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        // SUPER AGGRESSIVE LOGGING
        android.util.Log.e("ALARM_FIRED", "========== ALARM TRIGGERED ==========")
        android.util.Log.e("ALARM_FIRED", "Intent action: ${intent.action}")
        android.util.Log.e("ALARM_FIRED", "Time: ${System.currentTimeMillis()}")

        Log.e(TAG, "📬 ========== ALARM TRIGGERED ==========")
        Log.e(TAG, "📬 Intent: ${intent.action}")
        Log.e(TAG, "📬 Extras: ${intent.extras}")

        val notificationId = intent.getStringExtra("notification_id")
        val notificationType = intent.getStringExtra("notification_type") ?: "Notification"
        val message = intent.getStringExtra("notification_message") ?: "You have a notification"
        val deepLink = intent.getStringExtra("deep_link") ?: "notifications"

        Log.e(TAG, "📬 ID: $notificationId")
        Log.e(TAG, "📬 Type: $notificationType")
        Log.e(TAG, "📬 Message: $message")

        // Show the notification
        showNotification(context, notificationType, message, deepLink)

        // Track that notification was sent
        trackNotificationSent(context, notificationType)

        Log.e(TAG, "📬 ========== ALARM COMPLETE ==========")
    }

    private fun showNotification(
        context: Context,
        title: String,
        message: String,
        deepLink: String
    ) {
        try {
            Log.e(TAG, "🔔 Showing notification: $title")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("${AppConstant.DEEP_LINK_SCHEME}://$deepLink?type=${Uri.encode(title)}")
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
            Log.e(TAG, "✅ Notification shown: $title")
            android.util.Log.e("ALARM_FIRED", "✅ Notification shown: $title")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error showing notification", e)
            android.util.Log.e("ALARM_FIRED", "❌ Error: ${e.message}", e)
        }
    }

    private fun trackNotificationSent(context: Context, notificationType: String) {
        scope.launch {
            try {
                val dao = AppDatabase.getDatabase(context).analyticsDao()
                dao.insertAnalytic(
                    AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = "sent"
                    )
                )
                Log.e(TAG, "✅ Tracked sent: $notificationType")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error tracking sent", e)
            }
        }
    }
}
