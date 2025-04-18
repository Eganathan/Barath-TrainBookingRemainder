package dev.eknath.barathtrainbookingremainder.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trainNumber: String,
    val fromStation: String,
    val toStation: String,
    val departureDate: Date,
    val departureTime: String,
    val notes: String = "",
    val isAlarmSet: Boolean = false,
    val bookableDate: Date = Date() // Date when tickets become available for booking
)