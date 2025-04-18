package dev.eknath.barathtrainbookingremainder.data

import java.util.Date

data class Reminder(
    val id: Long = 0,
    val trainNumber: String,
    val fromStation: String,
    val toStation: String,
    val departureDate: Date,
    val departureTime: String,
    val notes: String = "",
    val isAlarmSet: Boolean = false
)