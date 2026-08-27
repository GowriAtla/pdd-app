package com.example.newapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val userName by viewModel.userName
    val userEmail by viewModel.userEmail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(24.dp)
    ) {
        ProfileHeader(userName, onBack)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Personal Information", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PersonalInfoList(userName, userEmail)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
        }
        
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PersonalInfoList(name: String, email: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            PersonalInfoItem(Icons.Default.Person, "Full Name", name)
            Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
            PersonalInfoItem(Icons.Default.Email, "Email Address", email)
        }
    }
}

@Composable
fun PersonalInfoItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Icon(icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, color = TextGray, fontSize = 12.sp)
            Text(text = value, color = Color.White, fontSize = 16.sp)
        }
    }
}
