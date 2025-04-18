package dev.eknath.barathtrainbookingremainder.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dev.eknath.barathtrainbookingremainder.data.Reminder
import java.util.Date

class ReminderViewModel : ViewModel() {

    private var _reminders = mutableStateListOf<Reminder>()
    val reminders: List<Reminder> get() = _reminders

    private var nextId = 1L

    init {
        // Add some sample data
        addReminder(
            Reminder(
                id = nextId++,
                trainNumber = "12601",
                fromStation = "Chennai Central",
                toStation = "Bengaluru",
                departureDate = Date(),
                departureTime = "08:00 AM"
            )
        )
        addReminder(
            Reminder(
                id = nextId++,
                trainNumber = "12602",
                fromStation = "Bengaluru",
                toStation = "Chennai Central",
                departureDate = Date(),
                departureTime = "10:30 PM",
                notes = "Return journey",
                isAlarmSet = true
            )
        )
    }

    fun addReminder(reminder: Reminder) {
        val newReminder = reminder.copy(id = nextId++)
        _reminders.add(newReminder)
    }

    fun getReminder(id: Long): Reminder? {
        return _reminders.find { it.id == id }
    }

    fun updateReminder(reminder: Reminder) {
        val index = _reminders.indexOfFirst { it.id == reminder.id }
        if (index != -1) {
            _reminders[index] = reminder
        }
    }

    fun deleteReminder(id: Long) {
        _reminders.removeIf { it.id == id }
    }
}
