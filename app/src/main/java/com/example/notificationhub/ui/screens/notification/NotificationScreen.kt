package com.example.notificationhub.ui.screens.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notificationhub.data.entity.NotificationConfig

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
        // Notification list
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
                        text = "Send Test Notification",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

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
            // Icon
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

            // Content
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

            // Toggle Switch
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

// Helper functions
private fun getIconEmoji(type: String): String {
    return when (type) {
        "Daily Reminder" -> "📅"
        "Weekly Summary" -> "📊"
        "Special Offers" -> "🎁"
        "Tips & Tricks" -> "💡"
        else -> "🔔"
    }
}


@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    MaterialTheme {
        NotificationItem(
            notification = NotificationConfig(
                id = "1",
                type = "Daily Reminder",
                time = "09:00",
                repeatInterval = "Every day",
                message = "Don't forget to check your daily tasks!",
                deepLink = "home",
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
                type = "Special Offers",
                time = "Random times",
                repeatInterval = "Disabled",
                message = "Check out our latest deals and discounts",
                deepLink = "home",
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
                    time = "09:00",
                    repeatInterval = "Every day",
                    message = "Don't forget to check your daily tasks!",
                    deepLink = "home",
                    isEnabled = true
                ),
                NotificationConfig(
                    id = "2",
                    type = "Weekly Summary",
                    time = "Monday 6:00",
                    repeatInterval = "Weekly",
                    message = "Your weekly progress report is ready",
                    deepLink = "analytics",
                    isEnabled = true
                ),
                NotificationConfig(
                    id = "3",
                    type = "Special Offers",
                    time = "Random times",
                    repeatInterval = "Disabled",
                    message = "Check out our latest deals and discounts",
                    deepLink = "home",
                    isEnabled = false
                ),
                NotificationConfig(
                    id = "4",
                    type = "Tips & Tricks",
                    time = "3:00",
                    repeatInterval = "Twice a week",
                    message = "Learn something new with our pro tips",
                    deepLink = "home",
                    isEnabled = false
                )
            ),
            onToggle = {},
            onTestNotification = {}
        )
    }
}
