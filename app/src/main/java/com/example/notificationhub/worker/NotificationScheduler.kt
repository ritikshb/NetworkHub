package com.example.notificationhub.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.notificationhub.data.entity.NotificationConfig
import java.util.*

object NotificationScheduler {

    private const val TAG = "NotifScheduler"

    fun scheduleNotification(context: Context, config: NotificationConfig) {
        android.util.Log.e("TESTLOG", "========== SCHEDULER CALLED ==========")

        Log.e(TAG, "📅 ========== SCHEDULING NOTIFICATION ==========")
        Log.e(TAG, "📅 Type: ${config.type}")
        Log.e(TAG, "📅 Time: ${config.time}")
        Log.e(TAG, "📅 Repeat: ${config.repeatInterval}")

        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Log.e(TAG, "📅 Got AlarmManager")

            // Parse time
            val calendar = Calendar.getInstance()
            val timeParts = config.time.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts.getOrNull(1)?.toInt() ?: 0

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            Log.e(TAG, "📅 Parsed time: $hour:$minute")
            Log.e(TAG, "📅 Calendar: ${calendar.time}")
            Log.e(TAG, "📅 Current: ${Calendar.getInstance().time}")
            Log.e(TAG, "📅 Alarm time millis: ${calendar.timeInMillis}")
            Log.e(TAG, "📅 Current time millis: ${System.currentTimeMillis()}")
            Log.e(TAG, "📅 Difference: ${calendar.timeInMillis - System.currentTimeMillis()} ms")

            // If time passed, add 1 day
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                Log.e(TAG, "⚠️ Time passed, adding 1 day")
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                Log.e(TAG, "📅 New time: ${calendar.time}")
            }

            // Create intent
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = "com.example.notificationhub.NOTIFICATION_ALARM"  // ✅ ADD ACTION
                putExtra("notification_id", config.id)
                putExtra("notification_type", config.type)
                putExtra("notification_message", config.message)
                putExtra("deep_link", config.deepLink)
            }
            Log.e(TAG, "📅 Created intent with action: ${intent.action}")

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                config.id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            Log.e(TAG, "📅 Created PendingIntent with request code: ${config.id.hashCode()}")

            // ✅ CRITICAL: Use setExactAndAllowWhileIdle for single alarms
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.e(TAG, "⏰ Using setExactAndAllowWhileIdle (Android 6+)")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    Log.e(TAG, "⏰ Using setExact (Android 5)")
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }

                Log.e(TAG, "✅ ✅ ✅ ALARM SET FOR: ${calendar.time}")
                Log.e(TAG, "✅ Alarm will fire in: ${(calendar.timeInMillis - System.currentTimeMillis()) / 1000} seconds")
                android.util.Log.e("TESTLOG", "✅ ALARM SET FOR: ${calendar.time}")
            } catch (e: SecurityException) {
                Log.e(TAG, "❌ SecurityException - need SCHEDULE_EXACT_ALARM permission", e)
                throw e
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ ERROR: ${e.message}", e)
            android.util.Log.e("TESTLOG", "❌ ERROR: ${e.message}", e)
            e.printStackTrace()
        }
    }

    fun cancelNotification(context: Context, id: String) {
        Log.e(TAG, "🗑️ Cancelling: $id")
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                action = "com.example.notificationhub.NOTIFICATION_ALARM"
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
            Log.e(TAG, "✅ Cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error cancelling", e)
        }
    }
}
