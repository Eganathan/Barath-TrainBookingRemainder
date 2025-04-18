package dev.eknath.barathtrainbookingremainder.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY departureDate ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
    
    @Query("SELECT * FROM reminders WHERE bookableDate <= :date AND isAlarmSet = 1")
    suspend fun getUpcomingReminders(date: Date): List<Reminder>
}