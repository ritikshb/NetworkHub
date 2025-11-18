package com.example.notificationhub.data.local.database


import androidx.room.*
import com.example.notificationhub.data.entity.AnalyticsData
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for analytics operations.
 * Provides methods to track, query, and manage notification interaction analytics.
 */
@Dao
interface AnalyticsDao {

    /**
     * Inserts a new analytics record into the database.
     * Used to track notification clicks and deliveries.
     *
     * @param analytic The analytics data to insert
     */
    @Insert
    suspend fun insertAnalytic(analytic: AnalyticsData)

    /**
     * Gets the total count of notification clicks across all types.
     * Emits updates whenever the click count changes.
     *
     * @return Flow emitting the total number of clicks
     */
    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'clicked'")
    fun getTotalClicks(): Flow<Int>

    /**
     * Gets the number of clicks for a specific notification type.
     * Emits updates whenever clicks for this type change.
     *
     * @param type The notification type to query (e.g., "Daily Reminder")
     * @return Flow emitting the click count for the specified type
     */
    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'clicked' AND notificationType = :type")
    fun getClicksByType(type: String): Flow<Int>

    /**
     * Gets the number of sent notifications for a specific type.
     * Used to calculate engagement rates (clicks/sent).
     *
     * @param type The notification type to query
     * @return Flow emitting the sent count for the specified type
     */
    @Query("SELECT COUNT(*) FROM analytics WHERE action = 'sent' AND notificationType = :type")
    fun getSentByType(type: String): Flow<Int>

    /**
     * Retrieves the most recent analytics records.
     * Limited to the last 100 entries, ordered by timestamp descending.
     *
     * @return Flow emitting a list of recent analytics data
     */
    @Query("SELECT * FROM analytics ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAnalytics(): Flow<List<AnalyticsData>>

    /**
     * Deletes all analytics records from the database.
     * Use with caution - this operation is irreversible.
     */
    @Query("DELETE FROM analytics")
    suspend fun clearAllAnalytics()
}

