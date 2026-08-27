package com.example.newapp.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.newapp.data.Prescription
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedulePrescriptionAlarms(prescription: Prescription) {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        // Generate alarms for the next 7 days for this prescription
        for (dayOffset in 0..7) {
            val dateCal = today.clone() as Calendar
            dateCal.add(Calendar.DAY_OF_YEAR, dayOffset)
            val currentTime = System.currentTimeMillis()

            if (dateCal.timeInMillis < prescription.startDate || dateCal.timeInMillis > prescription.endDate) {
                continue
            }

            for ((index, timeStr) in prescription.times.withIndex()) {
                val parts = timeStr.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toIntOrNull() ?: continue
                    val minute = parts[1].toIntOrNull() ?: continue
                    
                    val alarmCal = dateCal.clone() as Calendar
                    alarmCal.set(Calendar.HOUR_OF_DAY, hour)
                    alarmCal.set(Calendar.MINUTE, minute)
                    alarmCal.set(Calendar.SECOND, 0)
                    
                    if (alarmCal.timeInMillis > currentTime) {
                        scheduleAlarm(alarmCal.timeInMillis, prescription, index, dayOffset)
                    }
                }
            }
        }
    }

    private fun scheduleAlarm(timeInMillis: Long, prescription: Prescription, timeIndex: Int, dayOffset: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.newapp.REMINDER_ALARM"
            putExtra("prescriptionId", prescription.id)
            putExtra("medicineName", prescription.medicineName)
            putExtra("dosage", prescription.dosage)
            putExtra("timeStr", prescription.times[timeIndex])
        }

        val requestCode = (prescription.id.hashCode() + timeIndex * 100 + dayOffset).absoluteValue
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
            Log.d("AlarmScheduler", "Scheduled alarm for ${prescription.medicineName} at $timeInMillis")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing", e)
        }
    }

    fun snoozeAlarm(prescriptionId: String, medicineName: String, timeStr: String) {
        val snoozeTime = System.currentTimeMillis() + (10 * 60 * 1000) // 10 minutes
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.newapp.REMINDER_ALARM"
            putExtra("prescriptionId", prescriptionId)
            putExtra("medicineName", medicineName)
            putExtra("timeStr", timeStr)
            putExtra("isSnooze", true)
        }
        
        val requestCode = (prescriptionId.hashCode() + 9999).absoluteValue
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, snoozeTime, pendingIntent)
            }
        } catch (e: Exception) {
             Log.e("AlarmScheduler", "Exact alarm permission missing", e)
        }
    }

    private val Int.absoluteValue: Int get() = if (this < 0) -this else this
}
