package dev.eknath.barathtrainbookingremainder.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class ReminderRepository(private val reminderDao: ReminderDao) {
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun getReminderById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)
    }
    
    suspend fun getUpcomingReminders(date: Date): List<Reminder> {
        return reminderDao.getUpcomingReminders(date)
    }
}