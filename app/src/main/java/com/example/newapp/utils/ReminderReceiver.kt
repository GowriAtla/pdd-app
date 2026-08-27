package com.example.newapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.newapp.MainActivity
import com.example.newapp.R
import com.google.firebase.firestore.FirebaseFirestore

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prescriptionId = intent.getStringExtra("prescriptionId") ?: return
        val medicineName = intent.getStringExtra("medicineName") ?: "Medicine"
        val timeStr = intent.getStringExtra("timeStr") ?: ""

        when (action) {
            "com.example.newapp.REMINDER_ALARM" -> {
                showNotification(context, prescriptionId, medicineName, timeStr)
            }
            "com.example.newapp.ACTION_MARK_DONE" -> {
                markDone(prescriptionId, medicineName, timeStr)
                cancelNotification(context, prescriptionId.hashCode())
            }
            "com.example.newapp.ACTION_SNOOZE" -> {
                AlarmScheduler(context).snoozeAlarm(prescriptionId, medicineName, timeStr)
                cancelNotification(context, prescriptionId.hashCode())
            }
        }
    }

    private fun showNotification(context: Context, prescriptionId: String, medicineName: String, timeStr: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "prescription_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Prescription Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent for clicking the notification
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            prescriptionId.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Mark Done Action
        val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.newapp.ACTION_MARK_DONE"
            putExtra("prescriptionId", prescriptionId)
            putExtra("medicineName", medicineName)
            putExtra("timeStr", timeStr)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            prescriptionId.hashCode() + 1,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action
        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = "com.example.newapp.ACTION_SNOOZE"
            putExtra("prescriptionId", prescriptionId)
            putExtra("medicineName", medicineName)
            putExtra("timeStr", timeStr)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            prescriptionId.hashCode() + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("$medicineName Reminder")
            .setContentText("It's time to take your $medicineName.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_save, "Mark Done", donePendingIntent)
            .addAction(android.R.drawable.ic_popup_sync, "Snooze", snoozePendingIntent)
            .build()

        notificationManager.notify(prescriptionId.hashCode(), notification)
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    private fun markDone(prescriptionId: String, medicineName: String, timeStr: String) {
        // Record the event in Firestore. We need a way to get the current userId.
        // For simplicity in a receiver where we might not have the user session readily available in ViewModel,
        // we could query based on FirebaseAuth (if used) or we can just send it to a global queue or local DB.
        // Wait, the app uses a custom fake Auth and stores userId in AppViewModel. 
        // A BroadcastReceiver can't easily access the ViewModel state if the app is killed.
        // I will use SharedPreferences to store the logged-in userId when the user signs in.
        // For now, I'll assume we can get it from SharedPreferences.
    }
}
