package dev.eknath.barathtrainbookingremainder.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
//
//data class SelectedDateInfo(
//    val requiredBookingDate: Long = getTodayInMillisAt() + TimeUnit.DAYS.toMillis(61)
//) {
//    val actualBookingDate: Long
//        get() = resetTimeTo(requiredBookingDate)
//
//    val bookingOpenOn: Long
//        get() = actualBookingDate - TimeUnit.DAYS.toMillis(60)
//
//    val isBookableNow: Boolean
//        get() = (System.currentTimeMillis() >= bookingOpenOn)
//
//    val formattedDate: String?
//        get() = bookingOpenOn.takeIf { it > 0L }?.let { millis ->
//            val bookingDate = Calendar.getInstance().apply { timeInMillis = millis }
//            val today = Calendar.getInstance()
//            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
//
//            when {
//                isSameDay(bookingDate, today) -> "Today"
//                isSameDay(bookingDate, tomorrow) -> "Tomorrow"
//                else -> SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date(millis))
//            }
//        }
//
//    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
//        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
//                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
//    }
//}
//
//fun getTodayInMillisAt(hour: Int = 8, minute: Int = 30): Long {
//    val calendar = Calendar.getInstance().apply {
//        set(Calendar.HOUR_OF_DAY, hour)
//        set(Calendar.MINUTE, minute)
//        set(Calendar.SECOND, 0)
//        set(Calendar.MILLISECOND, 0)
//    }
//    return calendar.timeInMillis
//}