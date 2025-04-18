package dev.eknath.barathtrainbookingremainder

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class TrainReminderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}