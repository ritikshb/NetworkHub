package com.example.notificationhub.core

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notificationhub.tab.MainScreenWithTabs
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

/**
 * Root composable for the Notification Hub application.
 * Initializes the navigation system and creates ViewModels for all main screens.
 * ViewModels are remembered to survive recomposition and maintain state across configuration changes.
 *
 * @param navController Optional NavController for navigation
 */
@Composable
fun NotificationHubApp(navController: NavHostController = rememberNavController()) {
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

    MainScreenWithTabs(
        navController = navController,
        notificationsViewModel = notificationsViewModel,
        scheduleViewModel = scheduleViewModel,
        analyticsViewModel = analyticsViewModel
    )
}
