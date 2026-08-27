package com.example.newapp.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.components.AppTextField
import com.example.newapp.ui.components.GradientButton
import com.example.newapp.ui.theme.*
import com.example.newapp.utils.EmailManager

@Composable
fun SignInScreen(
    viewModel: AppViewModel,
    onSignInSuccess: () -> Unit,
    onCreateOneClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authError by viewModel.authError
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
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Sign in to your account",
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
                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@example.com",
                    leadingIcon = Icons.Default.Email
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Password", color = Color.White)
                    Text(
                        text = "Forgot Password?",
                        color = AccentTeal,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "........", color = TextGray) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextGray) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextGray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = Color.White.copy(alpha = 0.2f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (authError != null) {
                    Text(
                        text = authError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                GradientButton(
                    text = "Sign In",
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signIn(email, password, context, onSignInSuccess)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row {
            Text(text = "Don't have an account? ", color = TextGray)
            Text(
                text = "Create one",
                color = AccentTeal,
                modifier = Modifier.clickable { onCreateOneClick() }
            )
        }
    }
}
