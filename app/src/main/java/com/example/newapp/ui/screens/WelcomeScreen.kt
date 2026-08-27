package com.example.newapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.ui.components.GradientButton
import com.example.newapp.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onAlreadyHaveAccount: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = AccentTeal.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(AccentTeal.copy(alpha = 0.4f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Oral Care Monitoring",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Track Your Oral Care During Radiotherapy",
            color = TextGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row {
            repeat(3) { index ->
                Surface(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(if (index == 2) 10.dp else 8.dp),
                    shape = CircleShape,
                    color = if (index == 2) AccentTeal else AccentTeal.copy(alpha = 0.3f)
                ) {}
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        GradientButton(
            text = "Get Started",
            onClick = onGetStarted,
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onAlreadyHaveAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonSecondary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Already have account",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "© 2026 Oral Care Monitoring",
            color = TextGray,
            fontSize = 12.sp
        )
    }
}
