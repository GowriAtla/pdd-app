package com.example.newapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PainTrackerScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    var painLevel by remember { mutableFloatStateOf(3f) }
    var notes by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pain Tracker", color = Color.White) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardBackground.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val emoji = when {
                        painLevel <= 3f -> "😊"
                        painLevel <= 7f -> "😐"
                        else -> "😫"
                    }
                    val status = when {
                        painLevel <= 3f -> "Mild pain"
                        painLevel <= 7f -> "Moderate pain"
                        else -> "Severe pain"
                    }
                    
                    Text(text = emoji, fontSize = 60.sp)
                    Text(text = "${painLevel.toInt()}", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    Text(text = status, color = TextGray)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Slider(
                        value = painLevel,
                        onValueChange = { painLevel = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentTeal,
                            activeTrackColor = AccentTeal,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "😊 No pain", color = TextGray, fontSize = 12.sp)
                        Text(text = "😫 Worst", color = TextGray, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (painLevel > 7f) ErrorRed.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (painLevel > 7f) ErrorRed else WarningOrange)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (painLevel > 7f) ErrorRed else WarningOrange
                        ) {
                            Icon(
                                imageVector = if (painLevel > 7f) Icons.Default.Warning else Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (painLevel > 7f) "Severe Pain Detected" else "Moderate Pain Detected",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your pain level is ${painLevel.toInt()}/10. Please meet your doctor as soon as possible for a medical evaluation.",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { /* Dismiss */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Dismiss", color = Color.White)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Additional Notes (Optional)", color = Color.White, modifier = Modifier.align(Alignment.Start))
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("Describe your pain or any additional details...", color = TextGray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = Color.White.copy(alpha = 0.2f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { 
                    viewModel.addPainRecord(painLevel.toInt(), notes)
                    if (painLevel > 7f) {
                        Toast.makeText(context, "Please meet your doctor immediately!", Toast.LENGTH_LONG).show()
                    } else if (painLevel > 3f) {
                        Toast.makeText(context, "Recovering soon! Keep monitoring.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Great! You are doing well.", Toast.LENGTH_SHORT).show()
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentTeal)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Pain Record", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
