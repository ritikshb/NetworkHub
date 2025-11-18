package com.example.notificationhub.tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notificationhub.R
import com.example.notificationhub.ui.navigation.AppNavHost
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationPermissionDialog
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

/**
 * Main screen with tab navigation.
 * Displays a header with app title and three navigation tabs for accessing different screens.
 *
 * @param navController Navigation controller for managing screen navigation
 * @param notificationsViewModel ViewModel for the Notifications screen
 * @param scheduleViewModel ViewModel for the Schedule screen
 * @param analyticsViewModel ViewModel for the Analytics screen
 * @param modifier Optional modifier for customizing the layout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithTabs(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
    scheduleViewModel: ScheduleViewModel,
    analyticsViewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    // Track current route to highlight the active tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine which tab should be selected based on current route
    val selectedTabIndex = when (currentRoute) {
        Destination.NotificationScreen.route -> 0
        Destination.ScheduleScreen.route -> 1
        Destination.AnalyticsScreen.route -> 2
        else -> 0
    }

    val showDialog by notificationsViewModel.showPermissionDialog.collectAsState()

    LaunchedEffect(Unit) {
        notificationsViewModel.checkNotificationPermission()
    }

    if (showDialog) {
        NotificationPermissionDialog(
            onOpenSettings = {
                notificationsViewModel.dismissPermissionDialog()
            },
            onDismiss = {
                notificationsViewModel.dismissPermissionDialog()
            }
        )
    }


    Column(modifier = modifier.systemBarsPadding()) {
        AppHeader()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colorResource(R.color.grey_black),
            topBar = { TabBar(selectedTabIndex, navController) }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                notificationsViewModel = notificationsViewModel,
                scheduleViewModel = scheduleViewModel,
                analyticsViewModel = analyticsViewModel
            )
        }
    }
}

/**
 * Header section displaying the app name and tagline with gradient background.
 */
@Composable
private fun AppHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFB71C1C),
                        Color(0xFF00897B)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.manage_app_notification),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

/**
 * Tab bar displaying navigation tabs for different app sections.
 *
 * @param selectedTabIndex Index of the currently selected tab (0-2)
 * @param navController Navigation controller for handling tab clicks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabBar(
    selectedTabIndex: Int,
    navController: NavHostController
) {
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = colorResource(R.color.grey_black)
    ) {
        Destination.entries.forEachIndexed { index, destination ->
            TabItem(
                destination = destination,
                isSelected = selectedTabIndex == index,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(Destination.NotificationScreen.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Individual tab item with icon and label.
 *
 * @param destination The destination this tab represents
 * @param isSelected Whether this tab is currently selected
 * @param onClick Callback invoked when the tab is clicked
 */
@Composable
private fun TabItem(
    destination: Destination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = destination.iconResId),
                contentDescription = stringResource(id = destination.labelResId),
                modifier = Modifier.size(33.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = destination.labelResId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isSelected) {
                    colorResource(R.color.red)
                } else {
                    colorResource(R.color.white)
                }
            )
        }
    }
}
