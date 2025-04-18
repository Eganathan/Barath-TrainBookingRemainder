package dev.eknath.barathtrainbookingremainder.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.eknath.barathtrainbookingremainder.data.AppDatabase
import dev.eknath.barathtrainbookingremainder.data.Reminder
import dev.eknath.barathtrainbookingremainder.data.ReminderRepository
import dev.eknath.barathtrainbookingremainder.utils.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReminderRepository
    private val alarmScheduler = AlarmScheduler(application)

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    init {
        val reminderDao = AppDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDao)

        viewModelScope.launch {
            repository.allReminders.collect { remindersList ->
                _reminders.value = remindersList
            }
        }
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            val newId = repository.insertReminder(reminder)
            if (reminder.isAlarmSet) {
                // If the reminder needs an alarm, schedule it
                alarmScheduler.scheduleReminder(reminder.copy(id = newId))
            }
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
            if (reminder.isAlarmSet) {
                alarmScheduler.scheduleReminder(reminder)
            } else {
                alarmScheduler.cancelReminder(reminder.id)
            }
        }
    }

    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            val reminder = getReminderById(id) ?: return@launch
            repository.deleteReminder(reminder)
            alarmScheduler.cancelReminder(reminder.id)
        }
    }

    suspend fun getReminderById(id: Long): Reminder? {
        return repository.getReminderById(id)
    }
}