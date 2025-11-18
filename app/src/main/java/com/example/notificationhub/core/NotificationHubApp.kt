package com.example.notificationhub.core

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.notificationhub.tab.MainScreenWithTabs
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

/**
 * Root composable for the Notification Hub application.
 * Initializes the navigation system and creates ViewModels for all main screens.
 * ViewModels are remembered to survive recomposition and maintain state across configuration changes.
 */
@Composable
fun NotificationHubApp() {
    // Get application context for ViewModel initialization
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Initialize ViewModels with remember to survive recomposition
    val notificationsViewModel = remember {
        NotificationsViewModel(application)
    }

    val scheduleViewModel = remember {
        ScheduleViewModel(application)
    }

    val analyticsViewModel = remember {
        AnalyticsViewModel(application)
    }

    // Initialize navigation controller
    val navController = rememberNavController()

    // Launch main screen with bottom navigation tabs
    MainScreenWithTabs(
        navController = navController,
        notificationsViewModel = notificationsViewModel,
        scheduleViewModel = scheduleViewModel,
        analyticsViewModel = analyticsViewModel
    )
}
