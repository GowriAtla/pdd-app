package com.example.newapp.ui.screens

import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.newapp.data.AppViewModel
import com.example.newapp.data.Prescription
import com.example.newapp.ui.components.AppTextField
import com.example.newapp.ui.theme.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(viewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var isAnalyzing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }

    // Form State
    var frequency by remember { mutableIntStateOf(1) }
    var notes by remember { mutableStateOf("") }
    
    val defaultTimes = listOf("09:00", "14:00", "20:00")
    var times by remember { mutableStateOf(listOf("09:00")) }

    // Sync with existing prescription if available
    LaunchedEffect(viewModel.prescriptions) {
        val existing = viewModel.prescriptions.firstOrNull()
        if (existing != null && !viewModel.isNewUpload.value) {
            frequency = existing.frequency
            times = existing.times
            showForm = true
        }
    }

    // Update times list when frequency changes
    LaunchedEffect(frequency) {
        if (viewModel.isNewUpload.value || viewModel.prescriptions.isEmpty()) {
            val newTimes = mutableListOf<String>()
            for (i in 0 until frequency) {
                if (i < times.size) newTimes.add(times[i])
                else newTimes.add(defaultTimes.getOrNull(i) ?: "12:00")
            }
            times = newTimes
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isAnalyzing = true
            errorMessage = null
            
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        isAnalyzing = false
                        val text = visionText.text.lowercase()
                        val keywords = listOf("hospital", "clinic", "patient", "doctor", "dr.", "prescription", "rx", "bill", "apollo", "investigations", "pharmacy")
                        if (keywords.any { text.contains(it) }) {
                            viewModel.addPrescription(uri)
                            showForm = true
                            // Try to guess medicine name - just take first few words or leave blank for demo
                        } else {
                            errorMessage = "Invalid format: Document must be a medical bill or prescription."
                            viewModel.prescriptionUri.value = null
                            showForm = false
                        }
                    }
                    .addOnFailureListener { e ->
                        isAnalyzing = false
                        errorMessage = "Failed to analyze image format."
                    }
            } catch (e: Exception) {
                isAnalyzing = false
                errorMessage = "Invalid image file."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Prescription", color = Color.White) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (errorMessage != null) {
                    Surface(
                        color = Color.Red.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (showForm) 200.dp else 400.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = CardBackground.copy(alpha = 0.5f),
                    shadowElevation = 4.dp
                ) {
                    if (isAnalyzing) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = AccentBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Analyzing Document Format...", color = Color.White)
                        }
                    } else if (viewModel.prescriptionUri.value == null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                tint = Color.White.copy(alpha = 0.1f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "No image selected", color = TextGray)
                        }
                    } else {
                        AsyncImage(
                            model = viewModel.prescriptionUri.value,
                            contentDescription = "Prescription",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (viewModel.prescriptionUri.value == null) "Upload from Gallery" else "Change Image", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (showForm) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Prescription Details", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Frequency (Times per day)", color = Color.White, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (i in 1..3) {
                            Button(
                                onClick = { frequency = i },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (frequency == i) AccentBlue else CardBackground,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("$i")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Schedule Reminder Times", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(times) { index, timeStr ->
                    val label = when(index) {
                        0 -> if (frequency == 1) "Time" else "Morning"
                        1 -> "Afternoon"
                        2 -> "Evening"
                        3 -> "Night"
                        else -> "Time ${index+1}"
                    }
                    TimePickerRow(label = label, timeStr = timeStr) { newTime ->
                        val newTimes = times.toMutableList()
                        newTimes[index] = newTime
                        times = newTimes
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            val start = cal.timeInMillis
                            // Set duration to 10 years to effectively make it "until next upload"
                            cal.add(Calendar.YEAR, 10)
                            val end = cal.timeInMillis
                            
                            val prescription = Prescription(
                                medicineName = "Prescription",
                                dosage = "",
                                frequency = frequency,
                                times = times,
                                startDate = start,
                                endDate = end,
                                notes = "",
                                prescriptionUri = viewModel.prescriptionUri.value?.toString()
                            )
                            viewModel.savePrescription(prescription, context)
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Schedule", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerRow(label: String, timeStr: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val parts = timeStr.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, h, m ->
                onTimeSelected(String.format("%02d:%02d", h, m))
            },
            hour, minute, false
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { timePickerDialog.show() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val formattedTime = "${if (hour > 12) hour - 12 else if (hour == 0) 12 else hour}:${String.format("%02d", minute)} ${if (hour >= 12) "PM" else "AM"}"
            Text(formattedTime, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Schedule, contentDescription = null, tint = AccentBlue)
        }
    }
}
