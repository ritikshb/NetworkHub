package com.example.notificationhub.core

import android.app.Application
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.util.AppConstant
import com.example.notificationhub.worker.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Custom Application class for the Notification Hub app.
 * Handles app-wide initialization including database setup, notification channels,
 * and default notification configurations.
 */
class NotificationHubApplication : Application() {

    /**
     * Application-scoped coroutine scope for background operations.
     * Uses SupervisorJob to prevent child coroutine failures from affecting siblings.
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Lazily initialized Room database instance.
     * Created only when first accessed to optimize startup time.
     */
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    /**
     * Called when the application is starting.
     * Initializes notification channels and default notification configurations.
     */
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        initializeDefaultNotifications()
    }

    /**
     * Initializes default notification configurations on first app launch.
     * Only populates the database if it's empty to avoid duplicates.
     * Runs asynchronously to prevent blocking the main thread.
     */
    private fun initializeDefaultNotifications() {
        applicationScope.launch {
            try {
                val dao = database.notificationDao()
                val count = dao.getNotificationCount()

                // Only insert defaults if database is empty
                if (count == 0) {
                    val defaultNotifications = listOf(
                        NotificationConfig(
                            id = AppConstant.ID_DAILY_REMINDER,
                            type = AppConstant.TYPE_DAILY_REMINDER,
                            time = AppConstant.DEFAULT_TIME_DAILY,
                            repeatInterval = AppConstant.EVERY_DAY,
                            message = AppConstant.DEFAULT_MESSAGE_DAILY,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_WEEKLY_SUMMARY,
                            type = AppConstant.TYPE_WEEKLY_SUMMARY,
                            time = getString(R.string.monday_6_00_pm),
                            repeatInterval = AppConstant.REPEAT_WEEKLY,
                            message = AppConstant.DEFAULT_MESSAGE_WEEKLY,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_SPECIAL_OFFERS,
                            type = AppConstant.TYPE_SPECIAL_OFFERS,
                            time = getString(R.string.random_times),
                            repeatInterval = getString(R.string.disabled),
                            message = AppConstant.DEFAULT_MESSAGE_OFFERS,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_TIPS_TRICKS,
                            type = AppConstant.TYPE_TIPS_TRICKS,
                            time = getString(R.string._3_00_pm),
                            repeatInterval = AppConstant.REPEAT_TWICE_WEEKLY,
                            message = AppConstant.DEFAULT_MESSAGE_TIPS,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        )
                    )

                    dao.insertAll(defaultNotifications)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
