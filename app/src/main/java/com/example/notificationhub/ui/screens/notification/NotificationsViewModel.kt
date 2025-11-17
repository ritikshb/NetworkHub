package com.example.notificationhub.ui.screens.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.example.notificationhub.data.local.database.AppDatabase
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.repository.NotificationRepository
import com.example.notificationhub.worker.NotificationHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotificationRepository
    val notifications: Flow<List<NotificationConfig>>

    init {
        val dao = AppDatabase.getDatabase(application).notificationDao()
        repository = NotificationRepository(dao)
        notifications = repository.getAllNotifications()
        NotificationHelper.createNotificationChannel(application)
    }

    fun toggleNotification(notification: NotificationConfig) {
        viewModelScope.launch {
            try {
                val updatedNotification = notification.copy(isEnabled = !notification.isEnabled)
                repository.updateNotification(updatedNotification)

                if (updatedNotification.isEnabled) {
                    NotificationHelper.scheduleNotification(getApplication(), updatedNotification)
                } else {
                    NotificationHelper.cancelNotification(getApplication(), updatedNotification.id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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

