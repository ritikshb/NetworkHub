📱 Notification Hub
A modern Android application built with Jetpack Compose that allows users to schedule, manage, and track notifications with detailed analytics.

✨ Features
🔔 Notification Management
Schedule Notifications: Set custom notifications with specific times and repeat intervals

Four Notification Types:

Daily Reminder
Weekly Summary
Special Offers

Tips & Tricks

Flexible Scheduling: Daily, Weekly, and custom intervals
Toggle Control: Enable/disable notifications with a simple switch

📊 Analytics Dashboard
Real-time Tracking: Monitor notification clicks and delivery

Engagement Metrics:

Total clicks
Click-through rate per notification type
Overall engagement percentage
Visual Insights: Track notification performance with detailed statistics

🎨 Modern UI
Material 3 Design: Clean, modern interface following Material Design guidelines
Dark Theme: Eye-friendly dark theme throughout the app
Smooth Navigation: Bottom navigation with three main screens
Responsive Design: Optimized for various screen sizes

🏗️ Technical Architecture
Built With
Kotlin - Primary programming language
Jetpack Compose - Modern declarative UI framework
Room Database - Local data persistence

📂 Project Structure
app/
├── data/
│   ├── entity/           # Data models
│   ├── local/database/   # Room database
│   └── repository/       # Data repositories
├── ui/
│   └── screens/
│       ├── analytics/    # Analytics screen
│       ├── notification/ # Notification management
│       └── schedule/     # Schedule configuration
├── worker/
│   ├── NotificationScheduler.kt   # Alarm scheduling logic
│   ├── NotificationReceiver.kt    # Broadcast receiver
│   └── NotificationHelper.kt      # Notification utilities
└── util/                # Constants and utilities


Coroutines & Flow - Asynchronous programming

🗄️ Database Schema
NotificationConfig

id: String (Primary Key)
type: String (Notification type)
time: String (Scheduled time in 24-hour format)
repeatInterval: String (Daily/Weekly/etc.)
message: String (Notification message)
deepLink: String (Deep link target)
isEnabled: Boolean (Active status)

AnalyticsData

id: Int (Auto-generated)
notificationType: String
timestamp: Long
action: String (clicked/sent)

🚀 Getting Started
Prerequisites
Android Studio Hedgehog (2023.1.1) or later
Android SDK 34
Kotlin 1.9.0+
Minimum SDK: 30 (Android 11)
Target SDK: 36 (Android 16)

1.Clone the repository
1.git clone https://github.com/yourusername/notification-hub.git
2.cd notification-hub

2.Open in Android Studio

Open Android Studio
Select "Open an Existing Project"
Navigate to the cloned directory
Wait for Gradle sync to complete

3.Build and Run
Connect an Android device or start an emulator
Click "Run" or press Shift + F10

Configuration
Notification Permissions
The app requires the following permissions (automatically requested):

POST_NOTIFICATIONS - Display notifications
SCHEDULE_EXACT_ALARM - Schedule exact-time alarms
USE_EXACT_ALARM - Use exact alarm APIs
WAKE_LOCK - Wake device for notifications

Deep Linking
App supports deep linking with scheme: notificationhub://

🔧 Development
Key Technologies
UI Layer

Jetpack Compose for declarative UI
Material 3 theming
State management with StateFlow
Data Layer
Room for local database
Kotlin Coroutines for async operations
Repository pattern for data access
Background Work
AlarmManager for precise scheduling
BroadcastReceiver for alarm handling
Foreground-safe notification delivery

Made with ❤️ using Jetpack Compose

AlarmManager - Precise notification scheduling

MVVM Architecture - Clean separation of concerns
