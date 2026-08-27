package com.example.newapp.data

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

data class PainRecord(var level: Int = 0, var notes: String = "", var date: String = "", var emoji: String = "", var timestamp: Long = 0L)
data class WeightRecord(var weight: String = "", var change: String = "", var date: String = "", var timestamp: Long = 0L)
data class ActivityLog(var type: String = "", var detail: String = "", var date: String = "", var timestamp: Long = 0L)

data class Prescription(
    var id: String = "",
    var medicineName: String = "",
    var dosage: String = "",
    var frequency: Int = 1,
    var times: List<String> = emptyList(),
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var notes: String = "",
    var timestamp: Long = 0L,
    var prescriptionUri: String? = null
)

data class ReminderEvent(
    var id: String = "",
    var prescriptionId: String = "",
    var medicineName: String = "",
    var timeString: String = "",
    var dateString: String = "",
    var status: String = "Pending",
    var timestamp: Long = 0L
)

class AppViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    var userId = mutableStateOf("")
    var userName = mutableStateOf("")
    var userEmail = mutableStateOf("")

    // Auth State
    var authError = mutableStateOf<String?>(null)
    var prescriptionUri = mutableStateOf<Uri?>(null)
    var isNewUpload = mutableStateOf(false)

    // Schedule (Dynamically populated)
    val prescriptions = mutableStateListOf<Prescription>()
    val todayReminders = mutableStateListOf<ReminderEvent>()
    var nextReminderText = mutableStateOf("No upcoming reminders")
    var nextReminderMedicine = mutableStateOf("")

    // Pain Tracker
    val painRecords = mutableStateListOf<PainRecord>()
    
    // Weight Tracker
    val weightRecords = mutableStateListOf<WeightRecord>()
    
    // Activity Log (History)
    val historyLogs = mutableStateListOf<ActivityLog>()

    // Progress
    val completedDoses = mutableStateListOf<String>()
    
    private var painListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null
    private var prescriptionListener: ListenerRegistration? = null
    private var reminderEventListener: ListenerRegistration? = null
    
    init {
        if (userId.value.isNotEmpty()) {
            startListeners()
        }
    }

    private fun startListeners() {
        if (userId.value.isEmpty()) return

        painListener?.remove()
        historyListener?.remove()
        prescriptionListener?.remove()
        reminderEventListener?.remove()
        
        listenToPainRecords()
        listenToHistoryLogs()
        listenToPrescriptions()
        listenToReminderEvents()
    }

    private fun listenToPrescriptions() {
        prescriptionListener = db.collection("users").document(userId.value).collection("prescriptions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                prescriptions.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(Prescription::class.java)?.let { 
                        it.id = doc.id
                        prescriptions.add(it) 
                        // Set the prescriptionUri state if a URI exists and it's not a fresh upload
                        if (it.prescriptionUri != null && !isNewUpload.value) {
                            prescriptionUri.value = Uri.parse(it.prescriptionUri)
                        }
                    }
                }
                updateNextReminder()
            }
    }

    private fun listenToReminderEvents() {
        // Fetch events for today to update the schedule screen
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        reminderEventListener = db.collection("users").document(userId.value).collection("reminder_events")
            .whereEqualTo("dateString", todayStr)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                todayReminders.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(ReminderEvent::class.java)?.let {
                        it.id = doc.id
                        todayReminders.add(it)
                    }
                }
                updateNextReminder()
            }
    }


    private fun listenToPainRecords() {
        painListener = db.collection("users").document(userId.value).collection("pain_records")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("AppViewModel", "Listen failed.", e)
                    return@addSnapshotListener
                }
                painRecords.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(PainRecord::class.java)?.let { painRecords.add(it) }
                }
            }
    }



    private fun listenToHistoryLogs() {
        historyListener = db.collection("users").document(userId.value).collection("history_logs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                historyLogs.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.toObject(ActivityLog::class.java)?.let { historyLogs.add(it) }
                }
            }
    }



    fun addPainRecord(level: Int, notes: String) {
        val emoji = when {
            level <= 3 -> "😊"
            level <= 7 -> "😐"
            else -> "😫"
        }
        val todayStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val record = PainRecord(level, notes, todayStr, emoji, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("pain_records").add(record)
            .addOnFailureListener { e -> Log.e("AppViewModel", "Error adding pain record", e) }
        
        val log = ActivityLog("Pain Tracked", "Level $level ($emoji)", todayStr, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("history_logs").add(log)
            .addOnFailureListener { e -> Log.e("AppViewModel", "Error adding log", e) }

    }

    fun addWeightRecord(weight: String) {
        val todayStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val record = WeightRecord("$weight kg", "-0.1 kg", todayStr, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("weight_records").add(record)
            .addOnFailureListener { e -> Log.e("AppViewModel", "Error adding weight record", e) }
        
        val log = ActivityLog("Weight Logged", "$weight kg", todayStr, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("history_logs").add(log)
            .addOnFailureListener { e -> Log.e("AppViewModel", "Error adding log", e) }

    }

    fun addPrescription(uri: Uri) {
        isNewUpload.value = true
        prescriptionUri.value = uri
        val todayStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val log = ActivityLog("Prescription Uploaded", "New image added", todayStr, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("history_logs").add(log)
    }

    fun savePrescription(prescription: Prescription, context: android.content.Context) {
        prescription.timestamp = System.currentTimeMillis()
        isNewUpload.value = false
        
        // Remove existing prescriptions to implement "save until another one is uploaded"
        db.collection("users").document(userId.value).collection("prescriptions").get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                }
                
                // Add the new prescription
                db.collection("users").document(userId.value).collection("prescriptions").add(prescription)
                    .addOnSuccessListener { docRef ->
                        prescription.id = docRef.id
                        com.example.newapp.utils.AlarmScheduler(context).schedulePrescriptionAlarms(prescription)
                        
                        val todayStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                        val log = ActivityLog("Schedule Updated", "${prescription.frequency}x Daily", todayStr, System.currentTimeMillis())
                        db.collection("users").document(userId.value).collection("history_logs").add(log)
                    }
            }
    }

    fun markReminderDone(prescriptionId: String, medicineName: String, timeStr: String) {
        val todayStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val displayDate = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val event = ReminderEvent(
            prescriptionId = prescriptionId,
            medicineName = medicineName,
            timeString = timeStr,
            dateString = todayStr,
            status = "Completed",
            timestamp = System.currentTimeMillis()
        )
        // Check if event already exists and update, or add new
        db.collection("users").document(userId.value).collection("reminder_events").add(event)
        
        val log = ActivityLog("Medicine Taken", medicineName, displayDate, System.currentTimeMillis())
        db.collection("users").document(userId.value).collection("history_logs").add(log)
    }

    fun clearHistory() {
        db.collection("users").document(userId.value).collection("history_logs").get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.delete()
                }
            }
    }

    private fun updateNextReminder() {
        var nearestTime = Long.MAX_VALUE
        var nearestMed = ""
        var nearestStr = ""
        val now = java.util.Calendar.getInstance()
        
        val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val todayStr = sdfDate.format(now.time)
        val tomorrow = now.clone() as java.util.Calendar
        tomorrow.add(java.util.Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = sdfDate.format(tomorrow.time)

        for (p in prescriptions) {
            if (now.timeInMillis < p.startDate || now.timeInMillis > p.endDate) continue
            
            for (timeStr in p.times) {
                val parts = timeStr.split(":")
                if (parts.size != 2) continue
                
                val h = parts[0].toIntOrNull() ?: 0
                val m = parts[1].toIntOrNull() ?: 0
                
                val cal = now.clone() as java.util.Calendar
                cal.set(java.util.Calendar.HOUR_OF_DAY, h)
                cal.set(java.util.Calendar.MINUTE, m)
                cal.set(java.util.Calendar.SECOND, 0)
                
                var isTomorrow = false
                if (cal.timeInMillis <= now.timeInMillis) {
                    // Already passed today, check tomorrow
                    cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    isTomorrow = true
                }
                
                val eventDateStr = if (isTomorrow) tomorrowStr else todayStr
                // Check if already completed
                val completed = todayReminders.any { it.prescriptionId == p.id && it.timeString == timeStr && it.dateString == eventDateStr && it.status == "Completed" }
                
                if (!completed && cal.timeInMillis < nearestTime) {
                    nearestTime = cal.timeInMillis
                    nearestMed = p.medicineName
                    nearestStr = "${if(h > 12) h-12 else if(h==0) 12 else h}:${String.format("%02d", m)} ${if(h >= 12) "PM" else "AM"} ${if (isTomorrow) "Tomorrow" else "Today"}"
                }
            }
        }
        
        if (nearestTime != Long.MAX_VALUE) {
            nextReminderMedicine.value = nearestMed
            nextReminderText.value = nearestStr
        } else {
            nextReminderMedicine.value = ""
            nextReminderText.value = "No upcoming reminders"
        }
    }

    fun signUp(name: String, email: String, password: String, context: android.content.Context, onSuccess: () -> Unit) {
        authError.value = null
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    authError.value = "User already exists. Please sign in."
                } else {
                    val userData = mapOf(
                        "name" to name,
                        "email" to email,
                        "password" to password // Note: In real apps, never store plain text passwords
                    )
                    db.collection("users").add(userData)
                        .addOnSuccessListener { documentReference ->
                            userId.value = documentReference.id
                            userName.value = name
                            userEmail.value = email
                            
                            // Save userId to SharedPreferences for Receiver
                            val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                            prefs.edit().putString("userId", documentReference.id).apply()
                            
                            startListeners()
                            onSuccess()
                        }
                        .addOnFailureListener {
                            authError.value = "Registration failed. Try again."
                        }
                }
            }
            .addOnFailureListener {
                authError.value = "Connection error."
            }
    }

    fun signIn(email: String, password: String, context: android.content.Context, onSuccess: () -> Unit) {
        authError.value = null
        db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    authError.value = "Invalid email or password."
                } else {
                    val doc = documents.documents[0]
                    userId.value = doc.id
                    userName.value = doc.getString("name") ?: ""
                    userEmail.value = doc.getString("email") ?: ""
                    
                    // Save userId to SharedPreferences for Receiver
                    val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().putString("userId", doc.id).apply()
                    
                    startListeners()
                    onSuccess()
                }
            }
            .addOnFailureListener {
                authError.value = "Login failed."
            }
    }


}
