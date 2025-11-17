package com.example.notificationhub.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationConfig(
    @PrimaryKey val id: String,
    val type: String,
    val time: String,
    val repeatInterval: String,
    val message: String,
    val deepLink: String,
    val isEnabled: Boolean = false
)
