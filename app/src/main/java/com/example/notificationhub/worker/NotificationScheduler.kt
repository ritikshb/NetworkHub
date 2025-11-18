package com.example.notificationhub.worker

import android.content.Context
import androidx.work.*
import com.example.notificationhub.data.entity.NotificationConfig
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Scheduler for managing notification work requests using WorkManager.
 * WorkManager provides reliable background execution even when the app is closed.
 */
object NotificationScheduler {

    /**
     * Schedules a notification using WorkManager.
     * Creates a repeating work request that shows notifications at the specified time.
     *
     * @param context Application context
     * @param config Notification configuration with time and repeat settings
     */
    fun scheduleNotification(context: Context, config: NotificationConfig) {
        try {
            cancelNotification(context, config.id)

            val timeParts = config.time.split(":")
            val hour = timeParts[0].trim().toInt()
            val minute = timeParts.getOrNull(1)?.trim()?.toInt() ?: 0

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

            val repeatIntervalDays = when (config.repeatInterval.lowercase()) {
                "daily", "every day" -> 1L
                "weekly" -> 7L
                "twice a week" -> 3L
                else -> 1L
            }

            val workData = workDataOf(
                "title" to config.type,
                "message" to config.message,
                "deepLink" to config.deepLink
            )

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                repeatIntervalDays,
                TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(workData)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                config.id,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cancels a scheduled notification by its unique ID.
     *
     * @param context Application context
     * @param id Unique notification identifier
     */
    fun cancelNotification(context: Context, id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(id)
    }
}
