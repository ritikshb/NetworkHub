package com.example.notificationhub.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.notificationhub.core.NotificationHubApp
import com.example.notificationhub.data.entity.AnalyticsData
import com.example.notificationhub.data.local.database.AppDatabase
import com.example.notificationhub.ui.theme.NotificationHubTheme
import com.example.notificationhub.util.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main activity that serves as the entry point for the Notification Hub application.
 * Handles notification permissions, deep linking, and analytics tracking.
 */
class MainActivity : ComponentActivity() {

    /**
     * Launcher for requesting POST_NOTIFICATIONS permission on Android 13+.
     * Handles the result of the permission request.
     */
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result is handled automatically by the system
        // App will be able to show notifications if granted
    }

    /**
     * Called when the activity is first created.
     * Initializes the UI and requests necessary permissions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        handleDeepLinkIntent(intent)

        setContent {
            NotificationHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotificationHubApp()
                }
            }
        }
    }

    /**
     * Called when a new intent is received while the activity is already running.
     * Handles deep links when the app is in the foreground or background.
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLinkIntent(intent)
    }

    /**
     * Processes deep link intents from notification clicks.
     * Extracts the notification type and tracks the click event.
     *
     * @param intent The intent containing the deep link data
     */
    private fun handleDeepLinkIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            val notificationType = uri.getQueryParameter("type") ?: "Unknown"
            trackNotificationClick(notificationType)
        }
    }

    /**
     * Records a notification click event in the analytics database.
     * Runs on the IO dispatcher to avoid blocking the main thread.
     *
     * @param notificationType The type of notification that was clicked
     */
    private fun trackNotificationClick(notificationType: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dao = AppDatabase.getDatabase(this@MainActivity).analyticsDao()
                dao.insertAnalytic(
                    AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = AppConstant.CLICKED
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Requests POST_NOTIFICATIONS permission for Android 13 (API 33) and above.
     * For Android 12 and below, notification permission is granted by default.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // No action needed
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
