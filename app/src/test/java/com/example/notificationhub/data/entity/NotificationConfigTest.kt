package com.example.notificationhub.data.entity

import org.junit.Assert.*
import org.junit.Test

class NotificationConfigTest {

    @Test
    fun `notification config is created with correct values`() {
        // Arrange & Act
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Don't forget your tasks!",
            deepLink = "home",
            isEnabled = true
        )

        // Assert
        assertEquals("1", notification.id)
        assertEquals("Daily Reminder", notification.type)
        assertEquals("09:00", notification.time)
        assertTrue(notification.isEnabled)
    }

    @Test
    fun `notification defaults to disabled when isEnabled not specified`() {
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Test",
            deepLink = "home"
        )

        assertFalse(notification.isEnabled)
    }
}
