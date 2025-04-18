package dev.eknath.barathtrainbookingremainder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.eknath.barathtrainbookingremainder.data.Reminder
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(reminder: Reminder) {
        val bookableCalendar = Calendar.getInstance().apply {
            time = reminder.bookableDate
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 20)
            set(Calendar.SECOND, 0)
        }
        
        // Subtract 5 minutes for the notification
        bookableCalendar.add(Calendar.MINUTE, -5)
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("REMINDER_ID", reminder.id)
            putExtra("TRAIN_NUMBER", reminder.trainNumber)
            putExtra("FROM_STATION", reminder.fromStation)
            putExtra("TO_STATION", reminder.toStation)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(), // Use reminder ID as request code for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            // Check if we can schedule exact alarms on Android S and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        bookableCalendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm if permission not granted
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        bookableCalendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                // For Android R and below, we can use setExactAndAllowWhileIdle
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    bookableCalendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Handle the case where the app doesn't have SCHEDULE_EXACT_ALARM permission
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                bookableCalendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(reminderId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }
}