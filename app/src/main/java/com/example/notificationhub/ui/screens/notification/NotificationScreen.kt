package com.example.notificationhub.ui.screens.notification

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.util.AppConstant

/**
 * Screen to display and manage notifications.
 *
 * @param viewModel ViewModel handling the notification logic
 * @param modifier Modifier to customize layout
 */
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState(initial = emptyList())

    NotificationsScreenContent(
        notifications = notifications,
        onToggle = { viewModel.toggleNotification(it) },
        onTestNotification = { viewModel.sendTestNotification() },
        modifier = modifier
    )
}

/**
 * Content composable rendering the list of notifications and a test notification button.
 *
 * @param notifications List of notifications to display
 * @param onToggle Callback when a notification toggle is changed
 * @param onTestNotification Callback for sending a test notification
 * @param modifier Modifier to customize layout
 */
@Composable
fun NotificationsScreenContent(
    notifications: List<NotificationConfig>,
    onToggle: (NotificationConfig) -> Unit,
    onTestNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onToggle = { onToggle(notification) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onTestNotification,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.send_test_notification),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Composable representing a single notification item with toggle and info.
 *
 * @param notification Notification data to display
 * @param onToggle Callback for toggling notification enabled/disabled state
 */
@Composable
fun NotificationItem(
    notification: NotificationConfig,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFF424242),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getIconEmoji(notification.type),
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${notification.time} • ${notification.repeatInterval}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF00BCD4)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = notification.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF00BCD4),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFF424242)
                )
            )
        }
    }
}

/**
 * Provides an emoji icon for notification types for UI display.
 *
 * @param type Notification type string
 * @return Emoji string representing the notification type
 */
private fun getIconEmoji(type: String): String = when(type) {
    AppConstant.TYPE_DAILY_REMINDER -> "📅"
    AppConstant.TYPE_WEEKLY_SUMMARY -> "📊"
    AppConstant.TYPE_SPECIAL_OFFERS -> "🎁"
    AppConstant.TYPE_TIPS_TRICKS -> "💡"
    else -> "🔔"
}


@Composable
fun NotificationPermissionDialog(
        onOpenSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enable Notifications") },
        text = {
            Text("Notifications are disabled. Please enable notifications in the app settings to receive alerts.")
        },
        confirmButton = {
            TextButton(onClick = {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                onOpenSettings()
            }) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    MaterialTheme {
        NotificationItem(
            notification = NotificationConfig(
                id = "1",
                type = AppConstant.TYPE_DAILY_REMINDER,
                time = AppConstant.DEFAULT_TIME_DAILY,
                repeatInterval = AppConstant.EVERY_DAY,
                message = stringResource(R.string.don_t_forget_to_check_your_daily_tasks),
                deepLink = "",
                isEnabled = true
            ),
            onToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemDisabledPreview() {
    MaterialTheme {
        NotificationItem(
            notification = NotificationConfig(
                id = "3",
                type = AppConstant.TYPE_SPECIAL_OFFERS,
                time = "Random times",
                repeatInterval = "Disabled",
                message = "Check out our latest deals and discounts",
                deepLink = "",
                isEnabled = false
            ),
            onToggle = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    MaterialTheme {
        NotificationsScreenContent(
            notifications = listOf(
                NotificationConfig(
                    id = "1",
                    type = "Daily Reminder",
                    time = AppConstant.DEFAULT_TIME_DAILY,
                    repeatInterval = AppConstant.EVERY_DAY,
                    message = "Don't forget to check your daily tasks!",
                    deepLink = "",
                    isEnabled = true
                ),
                NotificationConfig(
                    id = "2",
                    type = AppConstant.TYPE_WEEKLY_SUMMARY,
                    time = "Monday 6:00",
                    repeatInterval = "Weekly",
                    message = "Your weekly progress report is ready",
                    deepLink = "",
                    isEnabled = true
                ),
                NotificationConfig(
                    id = "3",
                    type = AppConstant.TYPE_SPECIAL_OFFERS,
                    time = "Random times",
                    repeatInterval = "Disabled",
                    message = "Check out our latest deals and discounts",
                    deepLink = "",
                    isEnabled = false
                ),
                NotificationConfig(
                    id = "4",
                    type = AppConstant.TYPE_TIPS_TRICKS,
                    time = "3:00",
                    repeatInterval = "Twice a week",
                    message = "Learn something new with our pro tips",
                    deepLink = "",
                    isEnabled = false
                )
            ),
            onToggle = {},
            onTestNotification = {}
        )
    }
}
