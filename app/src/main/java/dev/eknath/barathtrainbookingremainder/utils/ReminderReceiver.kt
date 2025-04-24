package dev.eknath.barathtrainbookingremainder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.eknath.barathtrainbookingremainder.R

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "train_reminders"
        private const val TAG = "ReminderReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", 0)
        val trainNumber = intent.getStringExtra("TRAIN_NUMBER") ?: ""
        val fromStation = intent.getStringExtra("FROM_STATION") ?: ""
        val toStation = intent.getStringExtra("TO_STATION") ?: ""

        Log.d(TAG, "Received reminder broadcast: Train $trainNumber from $fromStation to $toStation")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Train Booking Reminders",
                importance
            ).apply {
                description = "Notifications for train booking reminders"
                enableVibration(true)
                enableLights(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)

                // Set the alarm sound
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(soundUri, audioAttributes)
            }

            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created with sound and vibration")
        }

        // Build and show notification
        val isTest = intent.getBooleanExtra("IS_TEST", false)
        val isAlarmTest = intent.getBooleanExtra("IS_ALARM_TEST", false)

        val contentTitle = when {
            isAlarmTest -> "Test Alarm Triggered"
            isTest -> "Test Notification"
            else -> "Train Booking Available Soon!"
        }

        val contentText = when {
            isAlarmTest -> "Alarm test successful! Train $trainNumber would be bookable now."
            isTest -> "This is a test notification for Train $trainNumber"
            else -> "Train $trainNumber from $fromStation to $toStation can be booked in 5 minutes"
        }

        // Get the alarm sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_MAX) // Max priority for alarms
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500, 200, 500))
            .setOngoing(true) // Make it persistent until dismissed
            .setOnlyAlertOnce(false) // Alert on every notification

        try {
            notificationManager.notify(reminderId.toInt(), notificationBuilder.build())
            Log.d(TAG, "Notification displayed with ID: ${reminderId.toInt()} and alarm sound")
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying notification", e)
        }
    }
}