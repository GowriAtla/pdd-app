package com.example.newapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.ui.components.AppTextField
import com.example.newapp.ui.components.GradientButton
import com.example.newapp.ui.theme.*
import com.example.newapp.utils.EmailManager

@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onVerifySuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val emailManager = remember { EmailManager() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /* Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = TextGray)
            }
        }

        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = AccentTeal.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Oral Care Monitoring",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (isOtpSent) "Verify OTP code" else "Reset your password",
            color = TextGray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = CardBackground.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (isOtpSent) {
                    // Success Message
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1B3D2F)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "A 6-digit OTP code has been sent to your email.",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Verify OTP",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enter the 6-digit OTP code sent to your email",
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    AppTextField(
                        value = email,
                        onValueChange = { },
                        label = "Email Address",
                        placeholder = "",
                        leadingIcon = Icons.Default.Email,
                        readOnly = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AppTextField(
                        value = otp,
                        onValueChange = { if (it.length <= 6) otp = it },
                        label = "OTP (6 Digits)",
                        placeholder = "000000",
                        leadingIcon = Icons.Default.VpnKey
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    GradientButton(
                        text = "Verify OTP",
                        onClick = {
                            if (otp == generatedOtp && otp.isNotEmpty()) {
                                onVerifySuccess()
                            } else {
                                Toast.makeText(context, "Invalid OTP code", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "← Back to Enter Email",
                        color = AccentTeal,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isOtpSent = false; otp = "" },
                        textAlign = TextAlign.Center
                    )

                } else {
                    Text(
                        text = "Forgot Password",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Enter your email to request an OTP code",
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    AppTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        placeholder = "agowri146@gmail.com",
                        leadingIcon = Icons.Default.Email
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    GradientButton(
                        text = "Send Reset Code",
                        onClick = {
                            if (email.isNotBlank()) {
                                val newOtp = (100000..999999).random().toString()
                                generatedOtp = newOtp
                                Toast.makeText(context, "Sending code...", Toast.LENGTH_SHORT).show()
                                emailManager.sendEmail(
                                    to = email,
                                    subject = "OTP - Oral Care Monitoring",
                                    content = "<h1>Your OTP</h1><p>Your 6-digit verification code is: <b>$newOtp</b></p>"
                                ) { success -> 
                                    if (!success) {
                                        // In a real app, handle error
                                    }
                                }
                                isOtpSent = true
                            } else {
                                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "← Back to Login",
                        color = AccentTeal,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBackToLogin() },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
