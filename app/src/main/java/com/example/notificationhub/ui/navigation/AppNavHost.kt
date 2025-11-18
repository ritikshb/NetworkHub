package com.example.notificationhub.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notificationhub.tab.Destination
import com.example.notificationhub.ui.screens.analytics.AnalyticsScreen
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationsScreen
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleScreen
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

/**
 * Navigation host for managing app screen navigation.
 * Defines all available routes and their corresponding composable screens.
 *
 * @param navController Navigation controller for handling navigation actions
 * @param modifier Optional modifier for customizing the NavHost layout
 * @param notificationsViewModel ViewModel for the Notifications screen
 * @param scheduleViewModel ViewModel for the Schedule screen
 * @param analyticsViewModel ViewModel for the Analytics screen
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    notificationsViewModel: NotificationsViewModel,
    scheduleViewModel: ScheduleViewModel,
    analyticsViewModel: AnalyticsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Destination.NotificationScreen.route,
        modifier = modifier
    ) {
        // Notifications screen - displays list of scheduled notifications
        composable(route = Destination.NotificationScreen.route) {
            NotificationsScreen(viewModel = notificationsViewModel)
        }

        // Schedule screen - allows creating and configuring new notifications
        composable(route = Destination.ScheduleScreen.route) {
            ScheduleScreen(viewModel = scheduleViewModel)
        }

        // Analytics screen - shows notification engagement metrics
        composable(route = Destination.AnalyticsScreen.route) {
            AnalyticsScreen(viewModel = analyticsViewModel)
        }
    }
}
