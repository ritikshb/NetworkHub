package com.example.notificationhub.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.notificationhub.data.entity.NotificationConfig
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY id ASC")
    fun getAllNotifications(): Flow<List<NotificationConfig>>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): NotificationConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationConfig)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationConfig>)

    @Update
    suspend fun updateNotification(notification: NotificationConfig)

    @Delete
    suspend fun deleteNotification(notification: NotificationConfig)

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationCount(): Int
}

