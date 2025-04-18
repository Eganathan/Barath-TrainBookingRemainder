package dev.eknath.barathtrainbookingremainder.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.eknath.barathtrainbookingremainder.data.AppDatabase
import dev.eknath.barathtrainbookingremainder.data.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val reminderDao = AppDatabase.getDatabase(context).reminderDao()
            val repository = ReminderRepository(reminderDao)
            val alarmScheduler = AlarmScheduler(context)
            
            // Reschedule all active reminders
            CoroutineScope(Dispatchers.IO).launch {
                val upcomingReminders = repository.getUpcomingReminders(Date())
                for (reminder in upcomingReminders) {
                    if (reminder.isAlarmSet) {
                        alarmScheduler.scheduleReminder(reminder)
                    }
                }
            }
        }
    }
}