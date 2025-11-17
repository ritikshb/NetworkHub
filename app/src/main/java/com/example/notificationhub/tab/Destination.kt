package com.example.notificationhub.tab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.notificationhub.util.AppConstant
import com.example.notificationhub.R

enum class Destination(
    val route: String,
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int
) {
    NotificationScreen(
        AppConstant.NOTIFICATION_SCREEN,
        R.string.notifications,
        R.drawable.bell_icon
    ),
    ScheduleScreen(AppConstant.SCHEDULE_SCREEN, R.string.schedule, R.drawable.calender_icon),
    AnalyticsScreen(AppConstant.ANALYTICS_SCREEN, R.string.analytics, R.drawable.analytics_icon)
}