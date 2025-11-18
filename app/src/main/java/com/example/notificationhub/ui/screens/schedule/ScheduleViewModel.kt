package com.example.notificationhub.ui.screens.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.data.repository.NotificationRepository
import com.example.notificationhub.worker.NotificationHelper
import com.example.notificationhub.worker.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotificationRepository

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    private val _testNotificationSent = MutableStateFlow(false)
    val testNotificationSent: StateFlow<Boolean> = _testNotificationSent

    init {
        val dao = AppDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(dao)
        NotificationHelper.createNotificationChannel(application)
    }

    /**
     * Saves or updates a notification configuration.
     * If notification with the same ID exists, it replaces it.
     */
    fun saveNotification(
        type: String,
        time: String,
        repeatInterval: String,
        message: String,
        deepLink: String
    ) {
        viewModelScope.launch {
            try {
                val deepLinkValue = when (deepLink) {
                    "Open Home Screen" -> "home"
                    "Open Analytics" -> "analytics"
                    "Open Schedule" -> "schedule"
                    else -> "home"
                }

                val notificationId = generateNotificationId(type)

                val notification = NotificationConfig(
                    id = notificationId,
                    type = type,
                    time = time,
                    repeatInterval = repeatInterval,
                    message = message,
                    deepLink = deepLinkValue,
                    isEnabled = true
                )

                repository.insertNotification(notification)
                NotificationScheduler.scheduleNotification(getApplication(), notification)

                _saveSuccess.value = true
                kotlinx.coroutines.delay(2000)
                _saveSuccess.value = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Sends an immediate test notification.
     */
    fun testNotification(type: String, message: String) {
        viewModelScope.launch {
            try {
                NotificationHelper.sendTestNotificationImmediate(getApplication())

                _testNotificationSent.value = true
                kotlinx.coroutines.delay(1500)
                _testNotificationSent.value = false

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Updates the time of an existing notification and reschedules it if enabled.
     *
     * @param notification Existing notification to update
     * @param newTime New time string in HH:mm format
     */
    fun updateNotificationTime(notification: NotificationConfig, newTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedNotification = notification.copy(time = newTime)
                repository.updateNotification(updatedNotification)

                if (updatedNotification.isEnabled) {
                    NotificationScheduler.cancelNotification(getApplication(), updatedNotification.id)
                    NotificationScheduler.scheduleNotification(getApplication(), updatedNotification)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Generate stable notification ID for known types to avoid duplicates.
     */
    private fun generateNotificationId(type: String): String {
        return when (type) {
            "Daily Reminder" -> "1"
            "Weekly Summary" -> "2"
            "Special Offers" -> "3"
            "Tips & Tricks" -> "4"
            else -> "notification_${UUID.randomUUID()}"
        }
    }
}
