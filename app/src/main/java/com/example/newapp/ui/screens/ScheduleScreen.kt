package com.example.newapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.data.Prescription
import com.example.newapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Schedule", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        val now = Calendar.getInstance().timeInMillis
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdfDate.format(now)

        // Generate today's schedule items
        data class ScheduleItem(val timeStr: String, val hour: Int, val minute: Int, val prescription: Prescription)
        val items = mutableListOf<ScheduleItem>()
        
        for (p in viewModel.prescriptions) {
            if (now >= p.startDate && now <= p.endDate) {
                for (t in p.times) {
                    val parts = t.split(":")
                    if (parts.size == 2) {
                        items.add(ScheduleItem(t, parts[0].toInt(), parts[1].toInt(), p))
                    }
                }
            }
        }
        
        items.sortBy { it.hour * 60 + it.minute }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    Text(
                        "No reminders scheduled for today.",
                        color = TextGray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(items) { item ->
                    val isDone = viewModel.todayReminders.any { 
                        it.prescriptionId == item.prescription.id && 
                        it.timeString == item.timeStr && 
                        it.dateString == todayStr && 
                        it.status == "Completed" 
                    }
                    
                    ScheduleCard(item, isDone) {
                        if (!isDone) {
                            viewModel.markReminderDone(item.prescription.id, item.prescription.medicineName, item.timeStr)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(item: Any, isDone: Boolean, onMarkDone: () -> Unit) {
    // We pass item as Any due to generic list in LazyColumn but it's ScheduleItem
    val scheduleItem = item.javaClass.getMethod("getPrescription").invoke(item) as Prescription
    val timeStr = item.javaClass.getMethod("getTimeStr").invoke(item) as String
    
    val parts = timeStr.split(":")
    val h = parts[0].toIntOrNull() ?: 0
    val m = parts[1].toIntOrNull() ?: 0
    val formattedTime = "${if (h > 12) h - 12 else if (h == 0) 12 else h}:${String.format("%02d", m)} ${if (h >= 12) "PM" else "AM"}"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground.copy(alpha = 0.5f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = formattedTime, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = if (isDone) TextGray else Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = scheduleItem.medicineName, fontSize = 16.sp, color = if (isDone) TextGray else Color.White)
                Text(text = scheduleItem.dosage, fontSize = 14.sp, color = TextGray)
            }
            
            Button(
                onClick = onMarkDone,
                enabled = !isDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDone) SuccessGreen.copy(alpha = 0.5f) else AccentBlue,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.2f),
                    disabledContentColor = SuccessGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isDone) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Done", color = SuccessGreen)
                } else {
                    Text("Mark Done", color = Color.White)
                }
            }
        }
    }
}
