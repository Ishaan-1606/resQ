package com.resq.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.resq.app.R
import com.resq.app.ui.navigation.Routes
import com.resq.app.ui.screens.alerts.AlertsScreen
import com.resq.app.ui.screens.analytics.AnalyticsScreen
import com.resq.app.ui.screens.control.ControlScreen
import com.resq.app.ui.screens.dashboard.DashboardScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Dashboard : BottomNavItem(Routes.DASHBOARD, Icons.Default.Dashboard, "Home")
    object Analytics : BottomNavItem(Routes.ANALYTICS, Icons.Default.Analytics, "Analytics")
    object Alerts : BottomNavItem(Routes.ALERTS, Icons.Default.Notifications, "Alerts")
    object Control : BottomNavItem(Routes.CONTROL, Icons.Default.VideogameAsset, "Control")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToSettings: () -> Unit) {
    val innerNavController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Text(" ResQ", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) { // Just call the function
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val items = listOf(BottomNavItem.Dashboard, BottomNavItem.Analytics, BottomNavItem.Alerts, BottomNavItem.Control)

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            innerNavController.navigate(screen.route) {
                                popUpTo(innerNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) { DashboardScreen(innerNavController) }
            composable(Routes.ANALYTICS) { AnalyticsScreen() }
            composable(Routes.ALERTS) { AlertsScreen(innerNavController) }
            composable(Routes.CONTROL) { ControlScreen() }
        }
    }
}
