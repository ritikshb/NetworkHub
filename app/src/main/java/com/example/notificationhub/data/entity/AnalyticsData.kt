package com.example.notificationhub.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing analytics data for notification interactions.
 * Stores information about notification clicks and deliveries for tracking user engagement.
 *
 * @property id Unique identifier for each analytics record (auto-generated)
 * @property notificationType Type of notification (e.g., "Daily Reminder", "Weekly Summary")
 * @property timestamp Unix timestamp in milliseconds when the event occurred
 * @property action Type of action performed: "clicked" for user interactions, "sent" for delivered notifications
 */
@Entity(tableName = "analytics")
data class AnalyticsData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val notificationType: String,
    val timestamp: Long,
    val action: String // "clicked" or "sent"
)
