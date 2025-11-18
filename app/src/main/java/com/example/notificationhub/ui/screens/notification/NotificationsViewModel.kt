package com.example.notificationhub.ui.screens.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.worker.NotificationHelper
import com.example.notificationhub.worker.NotificationScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to manage notifications list and scheduling.
 * Handles loading, enabling/disabling, and updating notifications in the database,
 * as well as scheduling and cancelling alarms.
 */
class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationDao = AppDatabase.getDatabase(application).notificationDao()

    private val _notifications = MutableStateFlow<List<NotificationConfig>>(emptyList())
    val notifications: StateFlow<List<NotificationConfig>> = _notifications.asStateFlow()

    init {
        loadNotifications()
        rescheduleEnabledNotifications()
    }

    /**
     * Loads all notifications from the database and updates the UI state.
     */
    private fun loadNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.getAllNotifications().collect { list ->
                _notifications.value = list
            }
        }
    }

    /**
     * Reschedules all notifications that are currently enabled.
     */
    private fun rescheduleEnabledNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                delay(1000) // Allow database to be ready

                val allNotifications = notificationDao.getAllNotifications().first()
                val enabledNotifications = allNotifications.filter { it.isEnabled }

                if (enabledNotifications.isEmpty()) return@launch

                enabledNotifications.forEach { notification ->
                    try {
                        NotificationScheduler.scheduleNotification(
                            context = getApplication(),
                            config = notification
                        )
                    } catch (e: SecurityException) {
                        // Handle required permission issue
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Toggles the enabled state of a notification,
     * updates the database, and schedules or cancels the alarm accordingly.
     *
     * @param notification NotificationConfig object to toggle
     */
    fun toggleNotification(notification: NotificationConfig) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedNotification = notification.copy(isEnabled = !notification.isEnabled)
                notificationDao.updateNotification(updatedNotification)

                if (updatedNotification.isEnabled) {
                    try {
                        NotificationScheduler.scheduleNotification(
                            context = getApplication(),
                            config = updatedNotification
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        NotificationScheduler.cancelNotification(
                            context = getApplication(),
                            id = updatedNotification.id
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Sends an immediate test notification for user feedback.
     */
    fun sendTestNotification() {
        viewModelScope.launch {
            try {
                NotificationHelper.sendTestNotificationImmediate(getApplication())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
