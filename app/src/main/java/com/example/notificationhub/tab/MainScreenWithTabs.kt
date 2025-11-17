package com.example.notificationhub.tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notificationhub.R
import com.example.notificationhub.ui.navigation.AppNavHost
import com.example.notificationhub.ui.screens.analytics.AnalyticsViewModel
import com.example.notificationhub.ui.screens.notification.NotificationsViewModel
import com.example.notificationhub.ui.screens.schedule.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithTabs(
    navController: NavHostController,
    notificationsViewModel: NotificationsViewModel,
    scheduleViewModel: ScheduleViewModel,
    analyticsViewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    // ✅ Track current route to highlight correct tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedTabIndex = when (currentRoute) {
        Destination.NotificationScreen.route -> 0
        Destination.ScheduleScreen.route -> 1
        Destination.AnalyticsScreen.route -> 2
        else -> 0
    }

    Column(
        modifier = modifier.systemBarsPadding()
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth()) {
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

        // Tabs and Content
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = colorResource(R.color.grey_black),
            topBar = {
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = colorResource(R.color.grey_black)
                ) {
                    Destination.entries.forEachIndexed { index, destination ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(Destination.NotificationScreen.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = destination.iconResId),
                                    contentDescription = stringResource(id = destination.labelResId),
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(id = destination.labelResId),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (index == selectedTabIndex) {
                                        colorResource(R.color.red)
                                    } else {
                                        colorResource(R.color.white)
                                    }
                                )
                            }
                        }
                    }
                }
            }
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

