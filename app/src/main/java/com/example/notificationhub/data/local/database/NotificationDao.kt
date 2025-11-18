package com.example.notificationhub.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notificationhub.data.entity.NotificationConfig
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for notification configuration operations.
 * Provides methods to create, read, update, and delete notification settings.
 */
@Dao
interface NotificationDao {

    /**
     * Retrieves all notification configurations from the database.
     * Results are ordered by ID in ascending order and emitted as a Flow for reactive updates.
     *
     * @return Flow emitting a list of all notification configurations
     */
    @Query("SELECT * FROM notifications ORDER BY id ASC")
    fun getAllNotifications(): Flow<List<NotificationConfig>>

    /**
     * Retrieves a specific notification configuration by its ID.
     *
     * @param id Unique identifier of the notification
     * @return The notification configuration, or null if not found
     */
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationConfig?

    /**
     * Inserts a new notification configuration into the database.
     * If a notification with the same ID exists, it will be replaced.
     *
     * @param notification The notification configuration to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationConfig)

    /**
     * Inserts multiple notification configurations in a single transaction.
     * If notifications with the same IDs exist, they will be replaced.
     *
     * @param notifications List of notification configurations to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationConfig>)

    /**
     * Updates an existing notification configuration in the database.
     * The notification is matched by its primary key (id).
     *
     * @param notification The notification configuration with updated values
     */
    @Update
    suspend fun updateNotification(notification: NotificationConfig)

    /**
     * Deletes a notification configuration from the database.
     *
     * @param notification The notification configuration to delete
     */
    @Delete
    suspend fun deleteNotification(notification: NotificationConfig)

    /**
     * Gets the total number of notification configurations stored in the database.
     *
     * @return The count of all notifications
     */
    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int
}
