package com.example.notificationhub.ui.screens.notification

import android.app.Application
import android.util.Log
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

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "NotificationsViewModel"
    private val notificationDao = AppDatabase.getDatabase(application).notificationDao()

    private val _notifications = MutableStateFlow<List<NotificationConfig>>(emptyList())
    val notifications: StateFlow<List<NotificationConfig>> = _notifications.asStateFlow()

    init {
        Log.d(TAG, "========== NotificationsViewModel initialized ==========")
        loadNotifications()
        rescheduleEnabledNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.getAllNotifications().collect { list ->
                _notifications.value = list
                Log.d(TAG, "📋 Loaded ${list.size} notifications")
                list.forEach { notification ->
                    Log.d(TAG, "📋   ${notification.type} - Enabled: ${notification.isEnabled}, Time: ${notification.time}")
                }
            }
        }
    }

    private fun rescheduleEnabledNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔄 ========== RESCHEDULE START ==========")
                delay(1000) // Wait for database to be ready

                Log.d(TAG, "🔄 Getting all notifications from database...")
                val allNotifications = notificationDao.getAllNotifications().first()
                Log.d(TAG, "🔄 Got ${allNotifications.size} total notifications")

                val enabledNotifications = allNotifications.filter { it.isEnabled }
                Log.d(TAG, "🔄 Found ${enabledNotifications.size} enabled notifications")

                if (enabledNotifications.isEmpty()) {
                    Log.d(TAG, "🔄 No enabled notifications to schedule")
                    return@launch
                }

                enabledNotifications.forEach { notification ->
                    Log.d(TAG, "🔄 ========================================")
                    Log.d(TAG, "🔄 Processing: ${notification.type}")
                    Log.d(TAG, "🔄 Time: ${notification.time}")
                    Log.d(TAG, "🔄 Repeat: ${notification.repeatInterval}")

                    try {
                        Log.d(TAG, "🔄 Calling NotificationScheduler.scheduleNotification...")

                        NotificationScheduler.scheduleNotification(
                            context = getApplication(),
                            config = notification
                        )

                        Log.d(TAG, "🔄 ✅ Successfully scheduled ${notification.type}")
                    } catch (e: SecurityException) {
                        Log.e(TAG, "❌ SecurityException for ${notification.type}", e)
                        Log.e(TAG, "❌ Need SCHEDULE_EXACT_ALARM permission!")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error scheduling ${notification.type}", e)
                        e.printStackTrace()
                    }
                }

                Log.d(TAG, "🔄 ========== RESCHEDULE COMPLETE ==========")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Fatal error in rescheduleEnabledNotifications", e)
                e.printStackTrace()
            }
        }
    }

    fun toggleNotification(notification: NotificationConfig) {
        Log.d(TAG, "🔄 ========== TOGGLE CALLED ==========")
        Log.d(TAG, "🔄 Notification: ${notification.type}")
        Log.d(TAG, "🔄 Current state: ${notification.isEnabled}")
        Log.d(TAG, "🔄 New state will be: ${!notification.isEnabled}")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedNotification = notification.copy(isEnabled = !notification.isEnabled)

                Log.d(TAG, "💾 Updating database...")
                notificationDao.updateNotification(updatedNotification)
                Log.d(TAG, "✅ Database updated")

                if (updatedNotification.isEnabled) {
                    Log.d(TAG, "✅ Enabling notification: ${updatedNotification.type}")
                    Log.d(TAG, "⏰ Time: ${updatedNotification.time}")
                    Log.d(TAG, "🔁 Repeat: ${updatedNotification.repeatInterval}")

                    try {
                        Log.d(TAG, "🎯 About to call NotificationScheduler.scheduleNotification...")

                        NotificationScheduler.scheduleNotification(
                            context = getApplication(),
                            config = updatedNotification
                        )

                        Log.d(TAG, "✅ scheduleNotification() returned successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ EXCEPTION in scheduleNotification: ${e.javaClass.simpleName}", e)
                        Log.e(TAG, "❌ Message: ${e.message}")
                        e.printStackTrace()
                    }

                    Log.d(TAG, "✅ Scheduling block completed")
                } else {
                    Log.d(TAG, "❌ Disabling notification: ${updatedNotification.type}")
                    try {
                        NotificationScheduler.cancelNotification(
                            context = getApplication(),
                            id = updatedNotification.id
                        )
                        Log.d(TAG, "✅ Cancellation completed")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error cancelling", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error in toggleNotification", e)
                e.printStackTrace()
            }
        }
    }



    fun sendTestNotification() {
        Log.d(TAG, "🧪 Test notification button clicked")
        viewModelScope.launch {
            try {
                NotificationHelper.sendTestNotificationImmediate(getApplication())
                Log.d(TAG, "✅ Test notification sent")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error sending test notification", e)
                e.printStackTrace()
            }
        }
    }

    fun updateNotificationTime(notification: NotificationConfig, newTime: String) {
        Log.d(TAG, "🕐 ========== UPDATE TIME CALLED ==========")
        Log.d(TAG, "🕐 Notification: ${notification.type}")
        Log.d(TAG, "🕐 Old time: ${notification.time}")
        Log.d(TAG, "🕐 New time: $newTime")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Update time in database
                val updatedNotification = notification.copy(time = newTime)
                notificationDao.updateNotification(updatedNotification)
                Log.d(TAG, "✅ Time updated in database")

                // If notification is enabled, reschedule it
                if (updatedNotification.isEnabled) {
                    Log.d(TAG, "🔄 Notification is enabled, rescheduling...")

                    // Cancel old alarm
                    NotificationScheduler.cancelNotification(getApplication(), updatedNotification.id)

                    // Schedule with new time
                    NotificationScheduler.scheduleNotification(
                        context = getApplication(),
                        config = updatedNotification
                    )

                    Log.d(TAG, "✅ Rescheduled with new time")
                } else {
                    Log.d(TAG, "ℹ️ Notification is disabled, no rescheduling needed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error updating time", e)
                e.printStackTrace()
            }
        }
    }

}
