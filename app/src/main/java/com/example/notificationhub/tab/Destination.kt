package com.example.notificationhub.tab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.notificationhub.R
import com.example.notificationhub.util.AppConstant

/**
 * Enum representing the main navigation destinations in the app.
 * Each destination corresponds to a bottom navigation tab with its route, label, and icon.
 *
 * @property route Unique identifier for navigation routing
 * @property labelResId String resource ID for the tab label text
 * @property iconResId Drawable resource ID for the tab icon
 */
enum class Destination(
    val route: String,
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int
) {
    /**
     * Notification management screen destination.
     * Displays the list of scheduled notifications with toggle controls.
     */
    NotificationScreen(
        route = AppConstant.NOTIFICATION_SCREEN,
        labelResId = R.string.notifications,
        iconResId = R.drawable.bell_icon
    ),

    /**
     * Schedule configuration screen destination.
     * Allows users to create and configure new notification schedules.
     */
    ScheduleScreen(
        route = AppConstant.SCHEDULE_SCREEN,
        labelResId = R.string.schedule,
        iconResId = R.drawable.calender_icon
    ),

    /**
     * Analytics dashboard screen destination.
     * Shows notification engagement metrics and statistics.
     */
    AnalyticsScreen(
        route = AppConstant.ANALYTICS_SCREEN,
        labelResId = R.string.analytics,
        iconResId = R.drawable.analytics_icon
    )
}
