package com.example.notificationhub.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.util.AppConstant

/**
 * Room database for the Notification Hub application.
 * Manages local storage for notification configurations and analytics data.
 *
 * Database version: 2
 * Entities: NotificationConfig, AnalyticsData
 */
@Database(
    entities = [NotificationConfig::class, AnalyticsData::class],
    version = AppConstant.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to notification configuration operations.
     *
     * @return DAO for notification CRUD operations
     */
    abstract fun notificationDao(): NotificationDao

    /**
     * Provides access to analytics data operations.
     *
     * @return DAO for analytics tracking and queries
     */
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        /**
         * Singleton instance of the database.
         * Volatile ensures visibility of changes across threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton database instance.
         * Creates the database if it doesn't exist, using double-checked locking for thread safety.
         *
         * @param context Application context for database creation
         * @return The singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppConstant.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
