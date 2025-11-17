package com.example.notificationhub

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.notificationhub.tab.MainScreenWithTabs
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

@Composable
fun NotificationHubApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val notificationsViewModel = remember {
        NotificationsViewModel(application)
    }
    val scheduleViewModel = remember {
        ScheduleViewModel(application)
    }
    val analyticsViewModel = remember {
        AnalyticsViewModel(application)
    }

    val navController = rememberNavController()

    MainScreenWithTabs(
        navController = navController,
        notificationsViewModel = notificationsViewModel,
        scheduleViewModel = scheduleViewModel,
        analyticsViewModel = analyticsViewModel
    )
}
