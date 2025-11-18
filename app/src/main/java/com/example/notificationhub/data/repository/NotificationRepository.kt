package com.example.notificationhub.data.repository

import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.NotificationDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing notification configurations.
 * Provides an abstraction layer between the ViewModel and the data source (Room database).
 * Centralizes data operations and can be extended to include remote data sources in the future.
 *
 * @property dao Data access object for notification operations
 */
class NotificationRepository(private val dao: NotificationDao) {

    /**
     * Retrieves all notification configurations as a reactive stream.
     * Automatically emits updates when the notification list changes.
     *
     * @return Flow emitting the current list of all notifications
     */
    fun getAllNotifications(): Flow<List<NotificationConfig>> {
        return dao.getAllNotifications()
    }

    /**
     * Updates an existing notification configuration in the database.
     * Used when toggling notifications on/off or modifying settings.
     *
     * @param notification The notification with updated values
     */
    suspend fun updateNotification(notification: NotificationConfig) {
        dao.updateNotification(notification)
    }

    /**
     * Inserts a new notification configuration into the database.
     * If a notification with the same ID exists, it will be replaced.
     *
     * @param notification The notification configuration to insert
     */
    suspend fun insertNotification(notification: NotificationConfig) {
        dao.insertNotification(notification)
    }
}
