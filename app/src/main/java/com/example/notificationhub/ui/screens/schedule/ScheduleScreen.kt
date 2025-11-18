package com.example.notificationhub.ui.screens.schedule

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.notificationhub.R
import com.example.notificationhub.data.entity.NotificationConfig
import com.example.notificationhub.util.AppConstant
import java.util.Calendar

/**
 * Screen composable showing schedule configuration UI.
 *
 * @param viewModel ViewModel to handle save and test notification actions
 * @param modifier Optional modifier for layout customization
 */
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
            Toast.makeText(
                context,
                context.getString(R.string.notification_saved_successfully),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(testSent) {
        if (testSent) {
            Toast.makeText(
                context,
                context.getString(R.string.test_notification_sent),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    ScheduleScreenContent(
        onSave = { type, time, repeat, message, deepLink ->
            viewModel.saveNotification(type, time, repeat, message, deepLink)
        },
        onTest = { type, message ->
            viewModel.testNotification(type, message)
        },
        onUpdateTime = { notification, newTime ->
            viewModel.updateNotificationTime(notification, newTime)
        },
        modifier = modifier
    )
}

/**
 * Content composable displaying the form inputs and action buttons for scheduling notifications.
 *
 * @param onSave Callback invoked to save notification configuration
 * @param onTest Callback invoked to send a test notification
 * @param modifier Optional modifier for layout customization
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreenContent(
    onSave: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> },
    onTest: (String, String) -> Unit = { _, _ -> },
    onUpdateTime: (notification: NotificationConfig, newTime: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
)  {
    var selectedType by remember { mutableStateOf(AppConstant.TYPE_DAILY_REMINDER) }
    var selectedTime by remember { mutableStateOf(AppConstant.DEFAULT_TIME_DAILY) }
    var selectedRepeat by remember { mutableStateOf(AppConstant.REPEAT_DAILY) }
    var message by remember { mutableStateOf("Your daily task is ready!") }
    var selectedDeepLink by remember { mutableStateOf("Open Home Screen") }
    var showTimePicker by remember { mutableStateOf(false) }
    var currentNotification by remember { mutableStateOf<com.example.notificationhub.data.entity.NotificationConfig?>(null) }

    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val newTime = String.format("%02d:%02d", hour, minute)
                showTimePicker = false
                selectedTime = newTime
                currentNotification?.let { notification ->
                    onUpdateTime(notification, newTime)
                }
            }
        )
    }

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
                        options = listOf(AppConstant.TYPE_DAILY_REMINDER, "Weekly Summary", "Special Offers", "Tips & Tricks"),
                        onValueChange = { selectedType = it }
                    )
                }
            }

            // Time - Now with TimePicker, update currentNotification to a dummy or previously saved notification
            item {
                FormField(label = "Time") {
                    TimePickerField(
                        value = selectedTime,
                        onClick = {
                            // Set currentNotification to some instance, here we create a dummy notification for demo
                            currentNotification = com.example.notificationhub.data.entity.NotificationConfig(
                                id = "dummy_id",
                                type = selectedType,
                                time = selectedTime,
                                repeatInterval = selectedRepeat,
                                message = message,
                                deepLink = selectedDeepLink,
                                isEnabled = true
                            )
                            showTimePicker = true
                        }
                    )
                }
            }

            // Repeat
            item {
                FormField(label = "Repeat") {
                    DropdownField(
                        value = selectedRepeat,
                        options = listOf(AppConstant.REPEAT_DAILY, "Weekly", "Twice a week"),
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
                        options = listOf("Open Notification Screen", "Open Analytics", "Open Schedule"),
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
                            imageVector = Icons.Default.DoubleArrow,
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
                    message = message,
                    time = selectedTime
                )
            }
        }
    }
}

/**
 * Composable to show a label and content in a column for form fields.
 *
 * @param label Label text for the form field
 * @param content Composable lambda for the field content
 */
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

/**
 * Dropdown field composable with options and a current value.
 *
 * @param value Current selected value
 * @param options List of available options
 * @param onValueChange Callback to notify on selection change
 */
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

/**
 * Read-only text field representing a time picker selector.
 *
 * @param value Current time string in HH:mm format
 * @param onClick Callback when the field is clicked
 */
@Composable
fun TimePickerField(
    value: String,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        readOnly = true,
        enabled = false,
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.AccessAlarm,
                    contentDescription = "Select Time",
                    tint = Color.White
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledContainerColor = Color(0xFF2A2A2A),
            disabledBorderColor = Color(0xFF424242),
            disabledTextColor = Color.White,
            disabledTrailingIconColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Dialog composable for selecting time (hour and minute).
 *
 * @param onDismiss Lambda called when dialog dismissed
 * @param onConfirm Lambda called with selected hour and minute on confirmation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF00BCD4))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("OK", color = Color(0xFF00BCD4))
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color(0xFF2A2A2A),
                    selectorColor = Color(0xFF00BCD4),
                    containerColor = Color(0xFF1A1A1A),
                    timeSelectorSelectedContainerColor = Color(0xFF00BCD4),
                    timeSelectorUnselectedContainerColor = Color(0xFF2A2A2A),
                    timeSelectorSelectedContentColor = Color.Black,
                    timeSelectorUnselectedContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF1A1A1A)
    )
}

/**
 * Card composable displaying a preview of the notification with details.
 *
 * @param type Notification type label
 * @param message Notification message content
 * @param time Notification scheduled time
 */
@Composable
fun NotificationPreviewCard(
    type: String,
    message: String,
    time: String = AppConstant.DEFAULT_TIME_DAILY
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF424242).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PREVIEW",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF00BCD4),
                    fontWeight = FontWeight.Medium
                )
            }

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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (message.isNotBlank()) message else "Your message will appear here...",
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isNotBlank()) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

// ===== Previews =====

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun DropdownFieldPreview() {
    MaterialTheme {
        DropdownField(
            value = AppConstant.TYPE_DAILY_REMINDER,
            options = listOf(AppConstant.TYPE_DAILY_REMINDER, "Weekly Summary", "Special Offers"),
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun TimePickerFieldPreview() {
    MaterialTheme {
        TimePickerField(
            value = AppConstant.DEFAULT_TIME_DAILY,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun NotificationPreviewCardPreview() {
    MaterialTheme {
        NotificationPreviewCard(
            type = AppConstant.TYPE_DAILY_REMINDER,
            message = "Your daily task is ready!",
            time = AppConstant.DEFAULT_TIME_DAILY
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScheduleScreenPreview() {
    MaterialTheme {
        ScheduleScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun TimePickerDialogPreview() {
    MaterialTheme {
        TimePickerDialog(
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}
