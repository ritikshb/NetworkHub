package com.example.notificationhub.ui.screens.analytics

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class AnalyticsUiState(
    val engagementToday: Int = 0,
    val totalClicks: Int = 0,
    val engagementRate: Float = 0.0f,
    val clicksByType: Map<String, Int> = emptyMap(),
    val bestEngagementTime: String = "9:00 - 10:00 AM"
)

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AnalyticsViewModel"
    private val analyticsDao = AppDatabase.getDatabase(application).analyticsDao()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "========== AnalyticsViewModel initialized ==========")
        loadAnalyticsData()
        debugPrintDatabase()
    }

    private fun loadAnalyticsData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "⏳ Loading analytics data...")

                combine(
                    analyticsDao.getTotalClicks(),
                    analyticsDao.getClicksByType("Daily Reminder"),
                    analyticsDao.getClicksByType("Weekly Summary"),
                    analyticsDao.getClicksByType("Special Offers"),
                    analyticsDao.getSentByType("Daily Reminder"),
                    analyticsDao.getSentByType("Weekly Summary"),
                    analyticsDao.getSentByType("Special Offers")
                ) { flows ->
                    val totalClicks = flows[0]
                    val dailyClicks = flows[1]
                    val weeklyClicks = flows[2]
                    val offersClicks = flows[3]
                    val dailySent = flows[4]
                    val weeklySent = flows[5]
                    val offersSent = flows[6]

                    Log.d(TAG, "📊 ========== ANALYTICS DATA ==========")
                    Log.d(TAG, "📊 Total Clicks: $totalClicks")
                    Log.d(TAG, "📊 Daily Reminder - Clicks: $dailyClicks, Sent: $dailySent")
                    Log.d(TAG, "📊 Weekly Summary - Clicks: $weeklyClicks, Sent: $weeklySent")
                    Log.d(TAG, "📊 Special Offers - Clicks: $offersClicks, Sent: $offersSent")

                    val clicksByType = mapOf(
                        "Daily Reminder" to dailyClicks,
                        "Weekly Summary" to weeklyClicks,
                        "Special Offers" to offersClicks
                    )

                    val totalSent = dailySent + weeklySent + offersSent
                    Log.d(TAG, "📊 Total Sent: $totalSent")

                    val engagementRate = if (totalSent > 0) {
                        (totalClicks.toFloat() / totalSent.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    }

                    val engagementToday = if (totalSent > 0) {
                        ((totalClicks.toFloat() / totalSent.toFloat()) * 100).toInt()
                    } else {
                        0
                    }

                    Log.d(TAG, "📊 Engagement Rate: $engagementRate (${(engagementRate * 100).toInt()}%)")
                    Log.d(TAG, "📊 Engagement Today: $engagementToday%")
                    Log.d(TAG, "📊 =====================================")

                    AnalyticsUiState(
                        engagementToday = engagementToday,
                        totalClicks = totalClicks,
                        engagementRate = engagementRate,
                        clicksByType = clicksByType,
                        bestEngagementTime = calculateBestEngagementTime()
                    )
                }
                    .distinctUntilChanged() // ✅ Only emit when values actually change
                    .collect { state ->
                        Log.d(TAG, "✅ UI State Updated: clicks=${state.totalClicks}, engagement=${state.engagementToday}%")
                        Log.d(TAG, "✅ Daily Reminder: ${state.clicksByType["Daily Reminder"]}")
                        _uiState.value = state
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading analytics", e)
                e.printStackTrace()
            }
        }
    }

    fun trackNotificationClick(notificationType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "🖱️ Tracking CLICK: '$notificationType'")
                analyticsDao.insertAnalytic(
                    AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = "clicked"
                    )
                )
                delay(100)
                Log.d(TAG, "✅ Click tracked successfully")
                debugPrintDatabase()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error tracking click", e)
            }
        }
    }

    fun trackNotificationSent(notificationType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Tracking SENT: '$notificationType'")
                analyticsDao.insertAnalytic(
                    AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = "sent"
                    )
                )
                delay(100)
                Log.d(TAG, "✅ Sent tracked successfully")
                debugPrintDatabase()
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error tracking sent", e)
            }
        }
    }

    fun refreshAnalytics() {
        Log.d(TAG, "🔄 Manually refreshing analytics")
        loadAnalyticsData()
    }

    fun clearAnalytics() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "🗑️ Clearing all analytics")
                analyticsDao.clearAllAnalytics()
                delay(100)
                Log.d(TAG, "✅ Analytics cleared")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error clearing analytics", e)
            }
        }
    }

    fun debugPrintDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                analyticsDao.getRecentAnalytics().first().let { list ->
                    Log.d(TAG, "🔍 ========== DATABASE CONTENTS ==========")
                    if (list.isEmpty()) {
                        Log.d(TAG, "🔍 Database is EMPTY!")
                    } else {
                        // Count by type and action
                        val dailyClicks = list.count { it.notificationType == "Daily Reminder" && it.action == "clicked" }
                        val dailySent = list.count { it.notificationType == "Daily Reminder" && it.action == "sent" }

                        Log.d(TAG, "🔍 Quick Summary:")
                        Log.d(TAG, "🔍   Daily Reminder - Clicks: $dailyClicks, Sent: $dailySent")
                        Log.d(TAG, "🔍 ")

                        list.take(10).forEachIndexed { index, analytic ->
                            Log.d(TAG, "🔍 Record #${index + 1}: ${analytic.notificationType} - ${analytic.action}")
                        }
                        Log.d(TAG, "🔍 Total records: ${list.size}")
                    }
                    Log.d(TAG, "🔍 =========================================")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error reading database", e)
            }
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
