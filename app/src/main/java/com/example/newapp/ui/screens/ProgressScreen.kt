package com.example.newapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val now = java.util.Calendar.getInstance().timeInMillis
    val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val todayStr = sdfDate.format(now)

    data class ProgressItem(val timeStr: String, val medicine: String, val isCompleted: Boolean, val sortTime: Int)
    val doses = mutableListOf<ProgressItem>()
    
    for (p in viewModel.prescriptions) {
        if (now >= p.startDate && now <= p.endDate) {
            for (t in p.times) {
                val completed = viewModel.todayReminders.any { 
                    it.prescriptionId == p.id && 
                    it.timeString == t && 
                    it.dateString == todayStr && 
                    it.status == "Completed" 
                }
                val parts = t.split(":")
                val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val formattedTime = "${if (h > 12) h - 12 else if (h == 0) 12 else h}:${String.format("%02d", m)} ${if (h >= 12) "PM" else "AM"}"
                
                doses.add(ProgressItem(formattedTime, p.medicineName, completed, h * 60 + m))
            }
        }
    }
    doses.sortBy { it.sortTime }
    
    val completedCount = doses.count { it.isCompleted }
    val progress = if (doses.isEmpty()) 0f else (completedCount.toFloat() / doses.size).coerceAtMost(1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Progress", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(24.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AccentBlue.copy(alpha = 0.8f)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Daily Progress", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = "$completedCount of ${doses.size} doses completed", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(text = "${(progress * 100).toInt()}%", color = Color.White, modifier = Modifier.align(Alignment.End), fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Dose Checklist", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            doses.forEach { dose ->
                DoseItem(dose.timeStr, dose.medicine, dose.isCompleted)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "This Week", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val isTodayCompleted = doses.isNotEmpty() && doses.size == completedCount
            val calendar = java.util.Calendar.getInstance()
            val todayIndex = (calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEachIndexed { index, day ->
                    val isToday = (index == todayIndex)
                    // Only mark as completed if it's today and all tasks are done. 
                    // Past days are not auto-marked now as per requirement "only the day should mark" 
                    // after completing all tasks.
                    val completed = isToday && isTodayCompleted
                    DayProgress(day, completed = completed, isToday = isToday)
                }
            }
        }
    }
}

@Composable
fun DoseItem(time: String, subtitle: String?, completed: Boolean) {
    Surface(
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = completed, 
                onClick = null, 
                modifier = Modifier.size(28.dp),
                colors = RadioButtonDefaults.colors(
                    selectedColor = SuccessGreen,
                    unselectedColor = TextGray
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = time, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                if (subtitle != null) {
                    Text(text = subtitle, color = TextGray, fontSize = 14.sp)
                }
            }
            if (completed) {
                Surface(color = SuccessGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(text = "Done", color = SuccessGreen, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DayProgress(day: String, completed: Boolean, isToday: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (completed) SuccessGreen.copy(alpha = 0.2f) else if (isToday) AccentBlue else Color.White.copy(alpha = 0.05f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (completed) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(28.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = day, fontSize = 14.sp, color = TextGray, fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal)
    }
}
