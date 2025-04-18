package dev.eknath.barathtrainbookingremainder.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.eknath.barathtrainbookingremainder.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("REMINDER_ID", 0)
        val trainNumber = intent.getStringExtra("TRAIN_NUMBER") ?: ""
        val fromStation = intent.getStringExtra("FROM_STATION") ?: ""
        val toStation = intent.getStringExtra("TO_STATION") ?: ""
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "train_reminders",
                "Train Booking Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for train booking reminders"
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        
        // Build and show notification
        val notificationBuilder = NotificationCompat.Builder(context, "train_reminders")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Train Booking Available Soon!")
            .setContentText("Train $trainNumber from $fromStation to $toStation can be booked in 5 minutes")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId.toInt(), notificationBuilder.build())
    }
}