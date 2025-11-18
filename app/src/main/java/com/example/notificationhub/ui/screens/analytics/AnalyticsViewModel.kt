package com.example.notificationhub.ui.screens.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.util.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * UI state data class for analytics screen.
 */
data class AnalyticsUiState(
    val engagementToday: Int = 0,
    val totalClicks: Int = 0,
    val engagementRate: Float = 0.0f,
    val clicksByType: Map<String, Int> = emptyMap(),
    val bestEngagementTime: String = "9:00 - 10:00 AM"
)

/**
 * ViewModel for presenting and calculating app analytics.
 * Collects and tracks notification engagement statistics from Room database.
 */
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val analyticsDao = AppDatabase.getDatabase(application).analyticsDao()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalyticsData()
    }

    /**
     * Loads and combines analytics data from the database,
     * updating the UI state with calculated metrics.
     */
    private fun loadAnalyticsData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                combine(
                    analyticsDao.getTotalClicks(),
                    analyticsDao.getClicksByType(AppConstant.TYPE_DAILY_REMINDER),
                    analyticsDao.getClicksByType(AppConstant.TYPE_WEEKLY_SUMMARY),
                    analyticsDao.getClicksByType(AppConstant.TYPE_SPECIAL_OFFERS),
                    analyticsDao.getSentByType(AppConstant.TYPE_DAILY_REMINDER),
                    analyticsDao.getSentByType(AppConstant.TYPE_WEEKLY_SUMMARY),
                    analyticsDao.getSentByType(AppConstant.TYPE_SPECIAL_OFFERS)
                ) { flows ->
                    val totalClicks = flows[0]
                    val dailyClicks = flows[1]
                    val weeklyClicks = flows[2]
                    val offersClicks = flows[3]
                    val dailySent = flows[4]
                    val weeklySent = flows[5]
                    val offersSent = flows[6]

                    val clicksByType = mapOf(
                        AppConstant.TYPE_DAILY_REMINDER to dailyClicks,
                        AppConstant.TYPE_WEEKLY_SUMMARY to weeklyClicks,
                        AppConstant.TYPE_SPECIAL_OFFERS to offersClicks
                    )

                    val totalSent = dailySent + weeklySent + offersSent

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

                    AnalyticsUiState(
                        engagementToday = engagementToday,
                        totalClicks = totalClicks,
                        engagementRate = engagementRate,
                        clicksByType = clicksByType,
                        bestEngagementTime = calculateBestEngagementTime()
                    )
                }
                    .distinctUntilChanged()
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Calculates the most likely time for highest user engagement.
     *
     * @return Time period string (e.g., "9:00 - 10:00")
     */
    private fun calculateBestEngagementTime(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour in 9..10 -> "9:00 - 10:00"
            hour in 14..15 -> "2:00 - 3:00"
            hour in 19..20 -> "7:00 - 8:00"
            else -> "9:00 - 10:00"
        }
    }
}
