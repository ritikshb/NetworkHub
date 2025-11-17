package com.example.notificationhub.ui.screens.schedule

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val testSent by viewModel.testNotificationSent.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Notification saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(testSent) {
        if (testSent) {
            Toast.makeText(context, "Test notification sent!", Toast.LENGTH_SHORT).show()
        }
    }

    ScheduleScreenContent(
        onSave = { type, time, repeat, message, deepLink ->
            viewModel.saveNotification(type, time, repeat, message, deepLink)
        },
        onTest = { type, message ->
            viewModel.testNotification(type, message)
        },
        modifier = modifier
    )
}

@Composable
fun ScheduleScreenContent(
    onSave: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onTest: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf("Daily Reminder") }
    var selectedTime by remember { mutableStateOf("09:00") }
    var selectedRepeat by remember { mutableStateOf("Daily") }
    var message by remember { mutableStateOf("Your daily task is ready!") }
    var selectedDeepLink by remember { mutableStateOf("Open Home Screen") }

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
            // Notification Type
            item {
                FormField(label = "Notification Type") {
                    DropdownField(
                        value = selectedType,
                        options = listOf("Daily Reminder", "Weekly Summary", "Special Offers", "Tips & Tricks"),
                        onValueChange = { selectedType = it }
                    )
                }
            }

            // Time
            item {
                FormField(label = "Time") {
                    TimeField(
                        value = selectedTime,
                        onValueChange = { selectedTime = it }
                    )
                }
            }

            // Repeat
            item {
                FormField(label = "Repeat") {
                    DropdownField(
                        value = selectedRepeat,
                        options = listOf("Daily", "Weekly", "Twice a week"),
                        onValueChange = { selectedRepeat = it }
                    )
                }
            }

            // Message
            item {
                FormField(label = "Message") {
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF2A2A2A),
                            unfocusedContainerColor = Color(0xFF2A2A2A),
                            focusedBorderColor = Color(0xFF00BCD4),
                            unfocusedBorderColor = Color(0xFF424242),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00BCD4)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                }
            }

            // Deep Link Action
            item {
                FormField(label = "Deep Link Action") {
                    DropdownField(
                        value = selectedDeepLink,
                        options = listOf("Open Home Screen", "Open Analytics", "Open Schedule"),
                        onValueChange = { selectedDeepLink = it }
                    )
                }
            }

            // Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onSave(selectedType, selectedTime, selectedRepeat, message, selectedDeepLink)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save")
                    }

                    OutlinedButton(
                        onClick = { onTest(selectedType, message) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        border = BorderStroke(1.dp, Color(0xFF00897B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Test", color = Color(0xFF00BCD4))
                    }
                }
            }

            // Preview Section
            item {
                NotificationPreviewCard(
                    type = selectedType,
                    message = message
                )
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun DropdownField(
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A2A2A),
                unfocusedContainerColor = Color(0xFF2A2A2A),
                focusedBorderColor = Color(0xFF00BCD4),
                unfocusedBorderColor = Color(0xFF424242),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF2A2A2A))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimeField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = Color.White
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2A2A2A),
            unfocusedContainerColor = Color(0xFF2A2A2A),
            focusedBorderColor = Color(0xFF00BCD4),
            unfocusedBorderColor = Color(0xFF424242),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color(0xFF00BCD4)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun NotificationPreviewCard(
    type: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF424242).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "PREVIEW",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Notification Hub",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (message.isNotBlank()) message else "Your message will appear here...",
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isNotBlank()) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

// ============ PREVIEWS ============

@Preview(showBackground = true)
@Composable
fun DropdownFieldPreview() {
    MaterialTheme {
        DropdownField(
            value = "Daily Reminder",
            options = listOf("Daily Reminder", "Weekly Summary", "Special Offers"),
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeFieldPreview() {
    MaterialTheme {
        TimeField(
            value = "09:00",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPreviewCardPreview() {
    MaterialTheme {
        Column(modifier = Modifier.background(Color(0xFF1A1A1A)).padding(16.dp)) {
            NotificationPreviewCard(
                type = "Daily Reminder",
                message = "Your daily task is ready!"
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScheduleScreenPreview() {
    MaterialTheme {
        ScheduleScreenContent()
    }
}
