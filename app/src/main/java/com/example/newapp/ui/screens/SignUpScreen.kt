package com.example.newapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.ui.components.AppTextField
import com.example.newapp.ui.components.GradientButton
import com.example.newapp.ui.theme.*

@Composable
fun SignUpScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authError by viewModel.authError
    var showExistsDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(authError) {
        if (authError == "User already exists. Please sign in.") {
            showExistsDialog = true
        }
    }

    if (showExistsDialog) {
        AlertDialog(
            onDismissRequest = { 
                showExistsDialog = false 
                viewModel.authError.value = null
            },
            title = { Text("Account Exists") },
            text = { Text("This email is already registered. Please sign in to your account.") },
            confirmButton = {
                TextButton(onClick = {
                    showExistsDialog = false
                    viewModel.authError.value = null
                    onSignInClick()
                }) {
                    Text("Go to Sign In")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExistsDialog = false
                    viewModel.authError.value = null
                }) {
                    Text("Close")
                }
            }
        )
    }

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
            text = "Create a new account",
            color = TextGray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            color = CardBackground.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                AppTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    placeholder = "Enter your name",
                    leadingIcon = Icons.Default.Person
                )

                AppTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@example.com",
                    leadingIcon = Icons.Default.Email
                )

                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Create a password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextGray
                            )
                        }
                    }
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
                    text = "Create Account",
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            viewModel.signUp(name, email, password, context, onSignUpSuccess)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row {
            Text(text = "Already have an account? ", color = TextGray)
            Text(
                text = "Sign In",
                color = AccentTeal,
                modifier = Modifier.clickable { onSignInClick() }
            )
        }
    }
}
