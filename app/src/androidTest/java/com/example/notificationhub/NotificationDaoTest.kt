package com.example.notificationhub

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.data.local.database.NotificationDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: NotificationDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.notificationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveNotification() = runTest {
        // Arrange
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Test message",
            deepLink = "home",
            isEnabled = true
        )

        // Act
        dao.insertNotification(notification)
        val retrieved = dao.getNotificationById("1")

        // Assert
        Assert.assertNotNull(retrieved)
        Assert.assertEquals("Daily Reminder", retrieved?.type)
        Assert.assertEquals("09:00", retrieved?.time)
    }

    @Test
    fun updateNotificationChangesValues() = runTest {
        // Arrange
        val notification = NotificationConfig(
            id = "1",
            type = "Daily Reminder",
            time = "09:00",
            repeatInterval = "Daily",
            message = "Original",
            deepLink = "home",
            isEnabled = false
        )
        dao.insertNotification(notification)

        // Act
        val updated = notification.copy(time = "10:00", isEnabled = true)
        dao.updateNotification(updated)
        val retrieved = dao.getNotificationById("1")

        // Assert
        Assert.assertNotNull(retrieved)
        Assert.assertEquals("10:00", retrieved?.time)
        Assert.assertEquals(true, retrieved?.isEnabled)
    }

    @Test
    fun getAllNotificationsReturnsAllRecords() = runTest {
        // Arrange
        val notifications = listOf(
            NotificationConfig("1", "Daily Reminder", "09:00", "Daily", "Test 1", "home", true),
            NotificationConfig("2", "Weekly Summary", "10:00", "Weekly", "Test 2", "home", false)
        )
        notifications.forEach { dao.insertNotification(it) }

        // Act
        val result = dao.getAllNotifications().first()

        // Assert
        Assert.assertEquals(2, result.size)
        Assert.assertEquals("Daily Reminder", result[0].type)
        Assert.assertEquals("Weekly Summary", result[1].type)
    }
}