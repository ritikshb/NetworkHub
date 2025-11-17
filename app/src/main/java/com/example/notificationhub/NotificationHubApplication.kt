package com.example.notificationhub

import android.app.Application
import android.util.Log
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.util.AppConstant
import com.example.notificationhub.worker.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NotificationHubApplication : Application() {

    private val TAG = "NotificationHubApp"
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        initializeDefaultNotifications()
    }

    private fun initializeDefaultNotifications() {
        applicationScope.launch {
            try {

                val dao = database.notificationDao()
                val count = dao.getNotificationCount()

                if (count == 0) {

                    val defaultNotifications = listOf(
                        NotificationConfig(
                            id = AppConstant.ID_DAILY_REMINDER,
                            type = AppConstant.TYPE_DAILY_REMINDER,
                            time = "9:00 AM",
                            repeatInterval = "Every day",
                            message = AppConstant.DEFAULT_MESSAGE_DAILY,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_WEEKLY_SUMMARY,
                            type = AppConstant.TYPE_WEEKLY_SUMMARY,
                            time = "Monday 6:00 PM",
                            repeatInterval = "Weekly",
                            message = AppConstant.DEFAULT_MESSAGE_WEEKLY,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_SPECIAL_OFFERS,
                            type = AppConstant.TYPE_SPECIAL_OFFERS,
                            time = "Random times",
                            repeatInterval = "Disabled",
                            message = AppConstant.DEFAULT_MESSAGE_OFFERS,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        ),
                        NotificationConfig(
                            id = AppConstant.ID_TIPS_TRICKS,
                            type = AppConstant.TYPE_TIPS_TRICKS,
                            time = "3:00 PM",
                            repeatInterval = "Twice a week",
                            message = AppConstant.DEFAULT_MESSAGE_TIPS,
                            deepLink = AppConstant.DEEP_LINK_NOTIFICATIONS,
                            isEnabled = false
                        )
                    )

                    dao.insertAll(defaultNotifications)

                    val newCount = dao.getNotificationCount()
                    Log.d(
                        TAG,
                        "Inserted ${defaultNotifications.size} notifications. New count: $newCount"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
