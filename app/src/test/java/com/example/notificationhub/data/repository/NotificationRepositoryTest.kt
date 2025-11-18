package com.example.notificationhub.data.repository


import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.NotificationDao
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

/**
 * Tests for NotificationRepository.
 */
class NotificationRepositoryTest {

    @Mock
    private lateinit var dao: NotificationDao

    private lateinit var repository: NotificationRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NotificationRepository(dao)
    }

    @Test
    fun `getAllNotifications returns data from dao`() = runTest {
        // Arrange
        val testNotifications = listOf(
            NotificationConfig(
                id = "1",
                type = "Daily Reminder",
                time = "09:00",
                repeatInterval = "Daily",
                message = "Test",
                deepLink = "home",
                isEnabled = true
            )
        )
        whenever(dao.getAllNotifications()).thenReturn(flowOf(testNotifications))

        // Act
        val result = repository.getAllNotifications().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Daily Reminder", result[0].type)
        verify(dao).getAllNotifications()
    }

    @Test
    fun `insertNotification calls dao insert`() = runTest {
        // Arrange
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Test",
            deepLink = "home",
            isEnabled = false
        )

        // Act
        repository.insertNotification(notification)

        // Assert
        verify(dao).insertNotification(notification)
    }

    @Test
    fun `updateNotification calls dao update`() = runTest {
        // Arrange
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Test",
            deepLink = "home",
            isEnabled = true
        )

        // Act
        repository.updateNotification(notification)

        // Assert
        verify(dao).updateNotification(notification)
    }
}
