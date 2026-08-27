package com.example.newapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("History", color = Color.White)
                        Text("Activity and completion log", color = TextGray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    if (viewModel.historyLogs.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Text("Clear All", color = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        val todayStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        val todayLogs = viewModel.historyLogs.filter { it.date == todayStr }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(24.dp)
        ) {
            CalendarCard()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Recent Activity", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (todayLogs.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = CardBackground.copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "No activities logged today.", color = TextGray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = todayStr,
                            color = AccentTeal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(todayLogs) { log ->
                        ActivityLogItem(log.type, log.detail)
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(type: String, detail: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = AccentTeal.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when(type) {
                        "Pain Tracked" -> Icons.Default.Timeline
                        "Weight Logged" -> Icons.Default.Scale
                        "Prescription Uploaded", "Prescription Added", "Schedule Updated" -> Icons.Default.History
                        "Medicine Taken" -> Icons.Default.CheckCircle
                        else -> Icons.Default.History
                    }
                    Icon(icon, contentDescription = null, tint = AccentTeal, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = type, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = detail, color = TextGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CalendarCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val displayMonth = "August 2026" 
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) { Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.White) }
                Text(text = displayMonth, color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(onClick = { }) { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val days = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                days.forEach { Text(text = it, color = TextGray, fontSize = 12.sp) }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column {
                // Calculation for August 2026 (Starts on Saturday, index 6)
                // Su Mo Tu We Th Fr Sa
                //                    1
                //  2  3  4  5  6  7  8
                repeat(6) { rowIndex ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        repeat(7) { colIndex ->
                            val day = rowIndex * 7 + colIndex - 5 // August 1, 2026 is a Saturday
                            if (day in 1..31) {
                                val isToday = day == 25 // Force 25 as today per request
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isToday) AccentBlue else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "$day", color = Color.White, fontSize = 12.sp)
                                }
                            } else {
                                Spacer(modifier = Modifier.size(36.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = AccentTeal) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Completed", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = AccentBlue) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Today", color = TextGray, fontSize = 12.sp)
            }
        }
    }
}
