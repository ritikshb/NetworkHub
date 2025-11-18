# Notification Hub

Notification Hub is an Android application that enables users to schedule customizable notifications with precise timing and repeat options. The app supports deep link navigation, notification previews, and immediate test notifications to enhance user engagement and productivity.

## Features

1. Schedule notifications with type, message, time (24-hour format), and repeat intervals  
2. Intuitive 24-hour Time Picker for accurate time selection  
3. Preview notifications before scheduling  
4. Background notification scheduling using WorkManager  
5. Test notifications instantly within the app  
6. Deep linking for in-app navigation on notification tap  
7. Persistent storage of notifications using Room database  

## Technical Overview

1. Built with Kotlin and Jetpack Compose for modern, declarative UI  
2. Uses WorkManager for scheduling  
3. Incorporates Room for local persistence of notifications and analytics tracking  
4. Implements BroadcastReceiver for notification firing  
5. Designed for Android 11 (SDK 30) and above, targeting SDK 36  
6. Requires POST_NOTIFICATIONS permission for Android 13+ notification delivery  

## Getting Started

Clone the repository:  
`git clone https://github.com/yourusername/notificationhub.git`  

Open the project in Android Studio.

----
### Additional Resources

- **Keystore file:** [Google Drive Link](https://drive.google.com/drive/folders/1W7kEPiMDsmfX5nltz3JV_kFRki3qOPD3?usp=sharing)   
  _Note: alias name: upload, storePassword: Notificationhub, keyPassword: Notificationhub_

- **Signed APK:** [APK Link](https://drive.google.com/drive/folders/1dgkoSy-GxFr5eU_Qmc6ZV_iTvPXZgVvS?usp=sharing)

- **Demo Video:** [Demo Video Link](https://drive.google.com/drive/folders/1faqSMEYRwyiZfDdmz2dDr6oQFZQnOe_L?usp=sharing)

---

Build and run the app from the release variant to test notification scheduling.
