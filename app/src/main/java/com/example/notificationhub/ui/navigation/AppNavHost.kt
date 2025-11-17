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
        composable(route = Destination.NotificationScreen.route) {
            NotificationsScreen(
                viewModel = notificationsViewModel,
                modifier = Modifier
            )
        }

        composable(route = Destination.ScheduleScreen.route) {
            ScheduleScreen(
                viewModel = scheduleViewModel,
                modifier = Modifier
            )
        }

        composable(route = Destination.AnalyticsScreen.route) {
            AnalyticsScreen(
                viewModel = analyticsViewModel,
                modifier = Modifier
            )
        }
    }
}
