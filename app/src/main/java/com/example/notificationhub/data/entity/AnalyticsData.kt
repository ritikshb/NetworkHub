package com.example.notificationhub.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analytics")
data class AnalyticsData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val notificationType: String,
    val timestamp: Long,
    val action: String
)
