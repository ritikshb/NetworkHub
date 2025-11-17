package com.example.notificationhub.ui.screens.schedule

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.data.repository.NotificationRepository
import com.example.notificationhub.worker.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ScheduleViewModel"
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

    fun saveNotification(
        type: String,
        time: String,
        repeatInterval: String,
        message: String,
        deepLink: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Saving notification: $type")

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
                Log.d(TAG, "Notification saved to DB")

                NotificationHelper.scheduleNotification(getApplication(), notification)
                Log.d(TAG, "Notification scheduled")

                _saveSuccess.value = true

                kotlinx.coroutines.delay(2000)
                _saveSuccess.value = false

            } catch (e: Exception) {
                Log.e(TAG, "Error saving notification", e)
                e.printStackTrace()
            }
        }
    }

    fun testNotification(type: String, message: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Sending test notification")

                val title = if (type.isNotBlank()) type else "Test Notification"
                val msg = if (message.isNotBlank()) message else "This is a test notification!"

                NotificationHelper.sendTestNotificationImmediate(getApplication())
                Log.d(TAG, "Test notification sent")

                _testNotificationSent.value = true

                kotlinx.coroutines.delay(1500)
                _testNotificationSent.value = false

            } catch (e: Exception) {
                Log.e(TAG, "Error sending test notification", e)
                e.printStackTrace()
            }
        }
    }

    private fun generateNotificationId(type: String): String {
        return when (type) {
            "Daily Reminder" -> "1"
            "Weekly Summary" -> "2"
            "Special Offers" -> "3"
            "Tips & Tricks" -> "4"
            else -> UUID.randomUUID().toString()
        }
    }
}
