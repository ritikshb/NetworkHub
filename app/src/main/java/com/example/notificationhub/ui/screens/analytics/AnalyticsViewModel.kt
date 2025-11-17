package com.example.notificationhub.ui.screens.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class AnalyticsUiState(
    val engagementToday: Int = 96,
    val totalClicks: Int = 0,
    val engagementRate: Float = 0.96f,
    val clicksByType: Map<String, Int> = emptyMap(),
    val bestEngagementTime: String = "9:00 - 10:00 AM"
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val analyticsDao = AppDatabase.getDatabase(application).analyticsDao()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch {
            // Combine all flows
            combine(
                analyticsDao.getTotalClicks(),
                analyticsDao.getClicksByType("Daily Reminder"),
                analyticsDao.getClicksByType("Weekly Summary"),
                analyticsDao.getClicksByType("Special Offers")
            ) { totalClicks, dailyClicks, weeklyClicks, offersClicks ->

                val clicksByType = mapOf(
                    "Daily Reminder" to dailyClicks,
                    "Weekly Summary" to weeklyClicks,
                    "Special Offers" to offersClicks
                )

                val engagementRate = if (totalClicks > 0) {
                    (totalClicks.toFloat() / (totalClicks + 10)) // Simple calculation
                } else {
                    0.96f // Default mock value
                }

                AnalyticsUiState(
                    engagementToday = 96, // Can be calculated based on today's data
                    totalClicks = totalClicks,
                    engagementRate = engagementRate.coerceIn(0f, 1f),
                    clicksByType = clicksByType,
                    bestEngagementTime = calculateBestEngagementTime()
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun trackNotificationClick(notificationType: String) {
        viewModelScope.launch {
            analyticsDao.insertAnalytic(
                AnalyticsData(
                    notificationType = notificationType,
                    timestamp = System.currentTimeMillis(),
                    action = "clicked"
                )
            )
        }
    }

    fun trackNotificationSent(notificationType: String) {
        viewModelScope.launch {
            analyticsDao.insertAnalytic(
                AnalyticsData(
                    notificationType = notificationType,
                    timestamp = System.currentTimeMillis(),
                    action = "sent"
                )
            )
        }
    }

    fun refreshAnalytics() {
        loadAnalyticsData()
    }

    fun clearAnalytics() {
        viewModelScope.launch {
            analyticsDao.clearAllAnalytics()
        }
    }

    private fun calculateBestEngagementTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 9..10 -> "9:00 - 10:00 AM"
            hour in 14..15 -> "2:00 - 3:00 PM"
            hour in 19..20 -> "7:00 - 8:00 PM"
            else -> "9:00 - 10:00 AM"
        }
    }
}
