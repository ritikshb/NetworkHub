package com.example.notificationhub.ui.screens.analytics

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notificationhub.R

// ==================== MAIN SCREEN ====================

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    AnalyticsScreenContent(
        uiState = uiState,
        viewModel = viewModel,
        modifier = modifier
    )
}

@Composable
fun AnalyticsScreenContent(
    uiState: AnalyticsUiState,
    viewModel: AnalyticsViewModel? = null,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Engagement cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EngagementCard(
                        value = "${uiState.engagementToday}%",
                        label = stringResource(R.string.engagement_today),
                        backgroundColor = Color(0xFFB71C1C),
                        modifier = Modifier.weight(1f)
                    )

                    EngagementCard(
                        value = uiState.totalClicks.toString(),
                        label = stringResource(R.string.total_clicks),
                        backgroundColor = Color(0xFF00897B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Engagement rate progress
            item {
                EngagementRateCard(engagementRate = uiState.engagementRate)
            }

            // Clicks by type
            item {
                ClicksByTypeCard(clicksByType = uiState.clicksByType)
            }

            // Best engagement time
            item {
                BestEngagementTimeCard(time = uiState.bestEngagementTime)
            }

            // Test buttons uncomment this block also for test functionality
//            if (viewModel != null) {
//                item {
//                    Spacer(modifier = Modifier.height(8.dp))
//                    TestAnalyticsCard(viewModel = viewModel)
//                }
//            }
        }
    }
}

@Composable
fun EngagementCard(
    value: String,
    label: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==================== ENGAGEMENT RATE CARD ====================

@Composable
fun EngagementRateCard(engagementRate: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Engagement Rate",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF424242))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(engagementRate)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFB71C1C),
                                    Color(0xFF00897B)
                                )
                            )
                        )
                ) {
                    if (engagementRate > 0.1f) {
                        Text(
                            text = "${(engagementRate * 100).toInt()}%",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "Target: 75%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "100%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ==================== CLICKS BY TYPE CARD ====================

@Composable
fun ClicksByTypeCard(clicksByType: Map<String, Int>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Clicks by Type",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ClickTypeItem("Daily Reminder", clicksByType["Daily Reminder"] ?: 0, "📅")
            ClickTypeItem("Weekly Summary", clicksByType["Weekly Summary"] ?: 0, "📊")
            ClickTypeItem("Special Offers", clicksByType["Special Offers"] ?: 0, "🎁")
        }
    }
}

@Composable
fun ClickTypeItem(title: String, count: Int, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color(0xFF424242),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF00BCD4),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BestEngagementTimeCard(time: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6B5B00)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row{
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = "Best Engagement Time",
                        modifier = Modifier.padding(0.dp,2.dp,0.dp,0.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


//if want to test db of analytics just un comment this code and use button and clear data you get how it works
//@Composable
//fun TestAnalyticsCard(viewModel: AnalyticsViewModel) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFF2A2A2A)
//        ),
//        shape = RoundedCornerShape(12.dp),
//        border = BorderStroke(1.dp, Color(0xFF424242))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Settings,
//                    contentDescription = null,
//                    tint = Color(0xFF00BCD4),
//                    modifier = Modifier.size(20.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    "Test Analytics",
//                    style = MaterialTheme.typography.titleMedium,
//                    color = Color.White,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            Text(
//                "Use these buttons to simulate analytics data",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.White.copy(alpha = 0.7f)
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Button(
//                    onClick = { viewModel.trackNotificationClick("Daily Reminder") },
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF00897B)
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Click", fontSize = 12.sp)
//                }
//
//                Button(
//                    onClick = { viewModel.trackNotificationSent("Daily Reminder") },
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF00897B)
//                    )
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Send,
//                        contentDescription = null,
//                        modifier = Modifier.size(16.dp)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Text("Sent", fontSize = 12.sp)
//                }
//            }
//
//            OutlinedButton(
//                onClick = { viewModel.clearAnalytics() },
//                modifier = Modifier.fillMaxWidth(),
//                border = BorderStroke(1.dp, Color(0xFFB71C1C)),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    contentColor = Color(0xFFB71C1C)
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = null,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Clear All Data")
//            }
//        }
//    }
//}



// ==================== PREVIEW METHODS ====================

@Preview(showBackground = true)
@Composable
fun EngagementCardPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier
                .background(Color(0xFF1A1A1A))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EngagementCard(
                value = "85%",
                label = "Engagement Today",
                backgroundColor = Color(0xFFB71C1C),
                modifier = Modifier.weight(1f)
            )
            EngagementCard(
                value = "124",
                label = "Total Clicks",
                backgroundColor = Color(0xFF00897B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EngagementRateCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            EngagementRateCard(engagementRate = 0.85f)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClicksByTypeCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            ClicksByTypeCard(
                clicksByType = mapOf(
                    "Daily Reminder" to 45,
                    "Weekly Summary" to 28,
                    "Special Offers" to 51
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BestEngagementTimeCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            BestEngagementTimeCard(time = "2:00 - 3:00 PM")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClickTypeItemPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFF2A2A2A))
                .padding(16.dp)
        ) {
            Column {
                ClickTypeItem("Daily Reminder", 45, "📅")
                ClickTypeItem("Weekly Summary", 28, "📊")
                ClickTypeItem("Special Offers", 51, "🎁")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnalyticsScreenPreview() {
    MaterialTheme {
        AnalyticsScreenContent(
            uiState = AnalyticsUiState(
                engagementToday = 85,
                totalClicks = 124,
                engagementRate = 0.85f,
                clicksByType = mapOf(
                    "Daily Reminder" to 45,
                    "Weekly Summary" to 28,
                    "Special Offers" to 51
                ),
                bestEngagementTime = "2:00 - 3:00 PM"
            ),
            viewModel = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnalyticsScreenEmptyPreview() {
    MaterialTheme {
        AnalyticsScreenContent(
            uiState = AnalyticsUiState(
                engagementToday = 0,
                totalClicks = 0,
                engagementRate = 0f,
                clicksByType = mapOf(
                    "Daily Reminder" to 0,
                    "Weekly Summary" to 0,
                    "Special Offers" to 0
                ),
                bestEngagementTime = "9:00 - 10:00 AM"
            ),
            viewModel = null
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnalyticsScreenHighEngagementPreview() {
    MaterialTheme {
        AnalyticsScreenContent(
            uiState = AnalyticsUiState(
                engagementToday = 100,
                totalClicks = 250,
                engagementRate = 1.0f,
                clicksByType = mapOf(
                    "Daily Reminder" to 100,
                    "Weekly Summary" to 80,
                    "Special Offers" to 70
                ),
                bestEngagementTime = "9:00 - 10:00 AM"
            ),
            viewModel = null
        )
    }
}
