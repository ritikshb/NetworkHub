package com.example.notificationhub.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a scheduled notification configuration.
 * Stores all settings required to schedule and display notifications at specific times.
 *
 * @property id Unique identifier for the notification configuration
 * @property type Type/category of notification (e.g., "Daily Reminder", "Weekly Summary", "Special Offers")
 * @property time Scheduled time in 24-hour format (HH:MM) when the notification should be triggered
 * @property repeatInterval Frequency of notification: "Daily", "Weekly", "Twice a week", or "Disabled"
 * @property message Content/body text displayed in the notification
 * @property deepLink Target screen to navigate when notification is clicked (e.g., "home", "analytics")
 * @property isEnabled Whether the notification is currently active and should be scheduled
 */
@Entity(tableName = "notifications")
data class NotificationConfig(
    @PrimaryKey
    val id: String,
    val type: String,
    val time: String,
    val repeatInterval: String,
    val message: String,
    val deepLink: String,
    val isEnabled: Boolean = false
)
