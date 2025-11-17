package com.example.notificationhub.data.local.database


import androidx.room.*
import com.example.notificationhub.data.entity.AnalyticsData
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {

    @Insert
    suspend fun insertAnalytic(analytic: AnalyticsData)

    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'clicked'")
    fun getTotalClicks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'clicked' AND notificationType = :type")
    fun getClicksByType(type: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'sent' AND notificationType = :type")
    fun getSentByType(type: String): Flow<Int>

    @Query("SELECT * FROM analytics ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAnalytics(): Flow<List<AnalyticsData>>

    @Query("DELETE FROM analytics")
    suspend fun clearAllAnalytics()
}
