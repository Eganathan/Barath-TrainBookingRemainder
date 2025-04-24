package dev.eknath.barathtrainbookingremainder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dev.eknath.barathtrainbookingremainder.data.Reminder
import java.util.Calendar
import java.util.Date

class AlarmScheduler(private val context: Context) {
    companion object {
        private const val TAG = "AlarmScheduler"
    }

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

        Log.d(TAG, "Scheduling reminder for train ${reminder.trainNumber} at ${bookableCalendar.time}")
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("REMINDER_ID", reminder.id)
            putExtra("TRAIN_NUMBER", reminder.trainNumber)
            putExtra("FROM_STATION", reminder.fromStation)
            putExtra("TO_STATION", reminder.toStation)
        }
        
        // Make sure to use FLAG_IMMUTABLE with newer Android versions
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(), // Use reminder ID as request code for uniqueness
            intent,
            pendingIntentFlags
        )
        
        try {
            // Check if we can schedule exact alarms on Android S and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Log.d(TAG, "Setting exact alarm (Android S+)")
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        bookableCalendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    Log.d(TAG, "Permission not available for exact alarms, using inexact alarm")
                    // Fall back to inexact alarm if permission not granted
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        bookableCalendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                // For Android R and below
                Log.d(TAG, "Setting exact alarm (pre-Android S)")
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    bookableCalendar.timeInMillis,
                    pendingIntent
                )
            }
            
            Log.d(TAG, "Alarm scheduled successfully for ID: ${reminder.id}")
        } catch (e: SecurityException) {
            // Handle the case where the app doesn't have SCHEDULE_EXACT_ALARM permission
            Log.e(TAG, "Security exception when scheduling exact alarm", e)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                bookableCalendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling alarm", e)
        }
    }

    fun cancelReminder(reminderId: Long) {
        val intent = Intent(context, ReminderReceiver::class.java)
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            pendingIntentFlags
        )
        
        pendingIntent?.let {
            try {
                alarmManager.cancel(it)
                it.cancel()
                Log.d(TAG, "Alarm cancelled for ID: $reminderId")
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling alarm", e)
            }
        }
    }

    /**
     * Creates a test notification that fires immediately for debugging purposes
     */
    fun testNotification() {
        Log.d(TAG, "Creating test notification")
        
        // Create a test reminder
        val testReminder = Reminder(
            id = System.currentTimeMillis(),
            trainNumber = "TEST-12345",
            fromStation = "Test Origin",
            toStation = "Test Destination",
            departureDate = Date(),
            departureTime = "08:15",
            notes = "Test notification",
            isAlarmSet = true,
            bookableDate = Date()
        )
        
        // Create intent for the notification
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("REMINDER_ID", testReminder.id)
            putExtra("TRAIN_NUMBER", testReminder.trainNumber)
            putExtra("FROM_STATION", testReminder.fromStation)
            putExtra("TO_STATION", testReminder.toStation)
            putExtra("IS_TEST", true)
        }
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            testReminder.id.toInt(),
            intent,
            pendingIntentFlags
        )
        
        try {
            // Schedule the alarm to fire in 5 seconds
            val triggerTime = System.currentTimeMillis() + 5000
            
            Log.d(TAG, "Setting test alarm to trigger in 5 seconds")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            
            Log.d(TAG, "Test alarm scheduled")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling test alarm", e)
        }
    }
}