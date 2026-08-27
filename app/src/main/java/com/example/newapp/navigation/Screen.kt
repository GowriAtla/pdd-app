package com.example.newapp.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object ForgotPassword : Screen("forgot_password")
    object ResetPassword : Screen("reset_password")
    object VerifyOtp : Screen("verify_otp")
    object Home : Screen("home")
    object Prescription : Screen("prescription")
    object Schedule : Screen("schedule")
    object PainTracker : Screen("pain_tracker")
    object Progress : Screen("progress")
    object History : Screen("history")
    object Profile : Screen("profile")
}
