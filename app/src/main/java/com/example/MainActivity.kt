package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AIGeneratorScreen
import com.example.ui.CalendarScreen
import com.example.ui.DashboardScreen
import com.example.ui.TrackerScreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SocialViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel = ViewModelProvider(this)[SocialViewModel::class.java]
            val currentTab by viewModel.currentTab.collectAsState()

            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(ElectricBlue, shape = androidx.compose.foundation.shape.CircleShape)
                                    )
                                    Text(
                                        text = "SOCIAL BOOST",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        letterSpacing = 2.sp,
                                        color = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            ),
                            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            windowInsets = WindowInsets.navigationBars,
                            modifier = Modifier.testTag("app_navigation_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == "Dashboard",
                                onClick = { viewModel.selectTab("Dashboard") },
                                label = { Text("Dashboard", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Dashboard") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = ElectricBlue,
                                    indicatorColor = ElectricBlue,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_dashboard")
                            )

                            NavigationBarItem(
                                selected = currentTab == "GrowthAI",
                                onClick = { viewModel.selectTab("GrowthAI") },
                                label = { Text("Growth AI", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = "Growth AI") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = ElectricBlue,
                                    indicatorColor = ElectricBlue,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_growth_ai")
                            )

                            NavigationBarItem(
                                selected = currentTab == "Tracker",
                                onClick = { viewModel.selectTab("Tracker") },
                                label = { Text("Tracker", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Tracker") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = ElectricBlue,
                                    indicatorColor = ElectricBlue,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_tracker")
                            )

                            NavigationBarItem(
                                selected = currentTab == "Calendar",
                                onClick = { viewModel.selectTab("Calendar") },
                                label = { Text("Calendar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    selectedTextColor = ElectricBlue,
                                    indicatorColor = ElectricBlue,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_tab_calendar")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "tab_fade_transition"
                        ) { tab ->
                            when (tab) {
                                "Dashboard" -> DashboardScreen(viewModel = viewModel)
                                "GrowthAI" -> AIGeneratorScreen(viewModel = viewModel)
                                "Tracker" -> TrackerScreen(viewModel = viewModel)
                                "Calendar" -> CalendarScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
