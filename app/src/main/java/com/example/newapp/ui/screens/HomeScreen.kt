package com.example.newapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.newapp.data.AppViewModel
import com.example.newapp.navigation.Screen
import com.example.newapp.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateTo: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onNavigateTo = onNavigateTo
            )
        },
        containerColor = BackgroundDark
    ) { padding ->
        val userName by viewModel.userName
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(16.dp)
        ) {
            HomeHeader(userName, onNavigateTo)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ReminderCard(viewModel, onNavigateTo)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Quick Access",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            QuickAccessGrid(onNavigateTo, Modifier.weight(1f))
        }
    }
}

@Composable
fun HomeHeader(userName: String, onNavigateTo: (String) -> Unit) {
    val calendar = java.util.Calendar.getInstance()
    val dayName = calendar.getDisplayName(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.LONG, java.util.Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Happy $dayName!",
                color = AccentTeal,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (userName.isNotEmpty()) "Hi, $userName!" else "Welcome!",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        IconButton(
            onClick = { onNavigateTo(Screen.Profile.route) },
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun ReminderCard(viewModel: AppViewModel, onNavigateTo: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Next Reminder", color = TextGray, fontSize = 16.sp)
                val medName = viewModel.nextReminderMedicine.value
                Text(text = if (medName.isNotEmpty()) medName else "No Meds", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = viewModel.nextReminderText.value, color = AccentTeal, fontSize = 16.sp)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row {
                    /* Removed hardcoded Mark Done / Snooze as they apply to specific alarms now. 
                       We could add a button to navigate to the schedule. */
                    Button(
                        onClick = { onNavigateTo(Screen.Schedule.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Schedule", color = Color.White)
                    }
                }
            }
        }
    }
}

data class QuickAccessItem(val title: String, val icon: ImageVector, val color: Color, val route: String)

@Composable
fun QuickAccessGrid(onNavigateTo: (String) -> Unit, modifier: Modifier = Modifier) {
    val items = listOf(
        QuickAccessItem("Prescription", Icons.Default.Description, Color(0xFF4A90E2), Screen.Prescription.route),
        QuickAccessItem("Schedule", Icons.Default.CalendarToday, Color(0xFF9013FE), Screen.Schedule.route),
        QuickAccessItem("Pain Tracker", Icons.Default.Timeline, Color(0xFFE91E63), Screen.PainTracker.route),
        QuickAccessItem("Progress", Icons.Default.TrendingUp, Color(0xFF50E3C2), Screen.Progress.route),
        QuickAccessItem("History", Icons.Default.History, Color(0xFFF5A623), Screen.History.route)
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(items) { item ->
            QuickAccessCard(item) { onNavigateTo(item.route) }
        }
    }
}

@Composable
fun QuickAccessCard(item: QuickAccessItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = CardBackground.copy(alpha = 0.5f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(16.dp),
                color = item.color.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun AppBottomNavigation(selectedTab: Int, onTabSelected: (Int) -> Unit, onNavigateTo: (String) -> Unit) {
    NavigationBar(
        containerColor = BackgroundDark,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, Screen.Home.route),
            Triple("Track", Icons.Default.MonitorHeart, Screen.PainTracker.route),
            Triple("Progress", Icons.Default.TrendingUp, Screen.Progress.route),
            Triple("Profile", Icons.Default.Person, Screen.Profile.route)
        )
        
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = (label == "Home" && selectedTab == 0) || (label == "Profile" && selectedTab == 3),
                onClick = { 
                    if (label == "Home") onTabSelected(0)
                    else if (label == "Profile") onTabSelected(3)
                    onNavigateTo(route)
                },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(30.dp)) },
                label = { Text(label, fontSize = 14.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentTeal,
                    selectedTextColor = AccentTeal,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = AccentTeal.copy(alpha = 0.1f)
                )
            )
        }
    }
}
