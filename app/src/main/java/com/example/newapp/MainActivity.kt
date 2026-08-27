package com.example.newapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.viewModels
import com.example.newapp.data.AppViewModel
import com.example.newapp.navigation.Screen
import com.example.newapp.ui.screens.*
import com.example.newapp.ui.theme.NewAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: AppViewModel) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.SignUp.route) },
                onAlreadyHaveAccount = { navController.navigate(Screen.SignIn.route) }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = { navController.navigate(Screen.Home.route) },
                onSignInClick = { navController.navigate(Screen.SignIn.route) }
            )
        }
        
        composable(Screen.SignIn.route) {
            SignInScreen(
                viewModel = viewModel,
                onSignInSuccess = { navController.navigate(Screen.Home.route) },
                onCreateOneClick = { navController.navigate(Screen.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }
        
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onVerifySuccess = { 
                    navController.navigate(Screen.ResetPassword.route)
                }
            )
        }
        
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = false }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateTo = { route -> navController.navigate(route) }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onLogout = { navController.navigate(Screen.Welcome.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                } },
                onBack = { navController.popBackStack() }
            )
        }
        
        // Screens
        composable(Screen.Prescription.route) { PrescriptionScreen(viewModel, navController::popBackStack) }
        composable(Screen.Schedule.route) { ScheduleScreen(viewModel, navController::popBackStack) }
        composable(Screen.PainTracker.route) { PainTrackerScreen(viewModel, navController::popBackStack) }
        composable(Screen.Progress.route) { ProgressScreen(viewModel, navController::popBackStack) }
        composable(Screen.History.route) { HistoryScreen(viewModel, navController::popBackStack) }
    }
}

