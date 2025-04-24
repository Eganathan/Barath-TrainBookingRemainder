package dev.eknath.barathtrainbookingremainder.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dev.eknath.barathtrainbookingremainder.data.AppDatabase
import dev.eknath.barathtrainbookingremainder.data.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Boot completed receiver triggered with action: ${intent.action}")
        
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Restoring alarms after device boot")
            val reminderDao = AppDatabase.getDatabase(context).reminderDao()
            val repository = ReminderRepository(reminderDao)
            val alarmScheduler = AlarmScheduler(context)
            
            // Reschedule all active reminders
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val upcomingReminders = repository.getUpcomingReminders(Date())
                    Log.d(TAG, "Found ${upcomingReminders.size} upcoming reminders to restore")
                    
                    for (reminder in upcomingReminders) {
                        if (reminder.isAlarmSet) {
                            Log.d(TAG, "Restoring alarm for reminder ID: ${reminder.id}, Train: ${reminder.trainNumber}")
                            alarmScheduler.scheduleReminder(reminder)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error restoring alarms", e)
                }
            }
        }
    }
}