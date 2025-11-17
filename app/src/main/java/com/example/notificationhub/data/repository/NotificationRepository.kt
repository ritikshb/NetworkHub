package com.example.notificationhub.data.repository


import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.NotificationDao
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val dao: NotificationDao) {

    fun getAllNotifications(): Flow<List<NotificationConfig>> {
        return dao.getAllNotifications()
    }

    suspend fun updateNotification(notification: NotificationConfig) {
        dao.updateNotification(notification)
    }

    suspend fun insertNotification(notification: NotificationConfig) {
        dao.insertNotification(notification)
    }

    suspend fun getNotificationById(id: String): NotificationConfig? {
        return dao.getNotificationById(id)
    }

    suspend fun insertAll(notifications: List<NotificationConfig>) {
        dao.insertAll(notifications)
    }
}

