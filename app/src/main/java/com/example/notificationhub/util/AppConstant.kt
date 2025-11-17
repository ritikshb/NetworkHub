package com.example.notificationhub.util

object AppConstant {
    const val NOTIFICATION_SCREEN = "NOTIFICATION_SCREEN"
    const val SCHEDULE_SCREEN = "SCHEDULE_SCREEN"
    const val ANALYTICS_SCREEN = "ANALYTICS_SCREEN"

    // Notification Channel Configuration
    const val NOTIFICATION_CHANNEL_ID = "notification_hub_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Notification Hub"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications from Notification Hub app"

    // Deep Link Scheme
    const val DEEP_LINK_SCHEME = "notificationhub"

    // Deep Link Destinations
    const val DEEP_LINK_HOME = "home"
    const val DEEP_LINK_ANALYTICS = "analytics"
    const val DEEP_LINK_SCHEDULE = "schedule"
    const val DEEP_LINK_NOTIFICATIONS = "notifications"

    // Work Manager Tags
    const val WORK_TAG_PREFIX = "notification_"

    // Notification Types (must match your UI)
    const val TYPE_DAILY_REMINDER = "Daily Reminder"
    const val TYPE_WEEKLY_SUMMARY = "Weekly Summary"
    const val TYPE_SPECIAL_OFFERS = "Special Offers"
    const val TYPE_TIPS_TRICKS = "Tips & Tricks"

    // Notification IDs (for Room database)
    const val ID_DAILY_REMINDER = "1"
    const val ID_WEEKLY_SUMMARY = "2"
    const val ID_SPECIAL_OFFERS = "3"
    const val ID_TIPS_TRICKS = "4"

    // Default Messages
    const val DEFAULT_MESSAGE_DAILY = "Don't forget to check your daily tasks!"
    const val DEFAULT_MESSAGE_WEEKLY = "Your weekly progress report is ready"
    const val DEFAULT_MESSAGE_OFFERS = "Check out our latest deals and discounts"
    const val DEFAULT_MESSAGE_TIPS = "Learn something new with our pro tips"

    // Repeat Intervals
    const val REPEAT_DAILY = "Daily"
    const val REPEAT_WEEKLY = "Weekly"
    const val REPEAT_TWICE_WEEKLY = "Twice a week"

    // Default Times
    const val DEFAULT_TIME_DAILY = "09:00"
    const val DEFAULT_TIME_WEEKLY = "18:00"
    const val DEFAULT_TIME_TIPS = "15:00"

    // SharedPreferences Keys (if you want to add preferences later)
    const val PREFS_NAME = "notification_hub_prefs"
    const val PREF_FIRST_LAUNCH = "first_launch"

    // Database
    const val DATABASE_NAME = "notification_hub_db"
    const val DATABASE_VERSION = 1
}

