package com.example.notificationhub.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notificationhub.core.NotificationHubApp
import com.example.notificationhub.ui.theme.NotificationHubTheme
import com.example.notificationhub.util.AppConstant
import kotlinx.coroutines.launch

/**
 * Main activity of the Notification Hub app.
 * Handles deep link navigation from notifications.
 */
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var deepLinkHandled: MutableState<Boolean>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NotificationHubTheme {
                navController = rememberNavController()
                deepLinkHandled = remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!deepLinkHandled.value) {
                        handleDeepLink(intent, navController)
                        deepLinkHandled.value = true
                    }
                }

                NotificationHubApp(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent, navController)
        deepLinkHandled.value = true
    }

    /**
     * Handles deep link from notification intent and navigates to the appropriate screen.
     *
     * @param intent Intent containing the deep link data
     * @param navController NavController for screen navigation
     */
    private fun handleDeepLink(
        intent: Intent?,
        navController: NavHostController
    ) {
        intent?.data?.let { uri ->
            when (uri.host) {
                "notifications" -> {
                    Log.d("DeeplinkText", "handleDeepLink: ${uri.host}")
                    navController.navigate(AppConstant.NOTIFICATION_SCREEN)
                }
                "analytics" -> {
                    Log.d("DeeplinkText", "handleDeepLink: ${uri.host}")
                    navController.navigate(AppConstant.ANALYTICS_SCREEN)
                }
                "schedule" -> {
                    Log.d("DeeplinkText", "handleDeepLink: ${uri.host}")
                    navController.navigate(AppConstant.SCHEDULE_SCREEN)
                }
                else ->{
                    Log.d("DeeplinkText", "handleDeepLink: ${uri.host}")
                    navController.navigate(AppConstant.NOTIFICATION_SCREEN)
                }
            }

            trackNotificationClick(uri)
        }
    }

    /**
     * Tracks notification click event for analytics.
     *
     * @param uri Deep link URI containing notification type
     */
    private fun trackNotificationClick(uri: Uri) {
        val notificationType = uri.getQueryParameter("type") ?: return

        // Track click in database
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val dao = com.example.notificationhub.data.local.database.AppDatabase
                    .getDatabase(applicationContext)
                    .analyticsDao()

                dao.insertAnalytic(
                    com.example.notificationhub.data.entity.AnalyticsData(
                        notificationType = notificationType,
                        timestamp = System.currentTimeMillis(),
                        action = "clicked"
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
