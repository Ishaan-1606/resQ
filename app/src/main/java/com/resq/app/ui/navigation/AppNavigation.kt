package com.resq.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.resq.app.ui.screens.LoginScreen
import com.resq.app.ui.screens.MainScreen
import com.resq.app.ui.screens.SplashScreen
import com.resq.app.ui.screens.settings.SettingsScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"
    const val SETTINGS = "settings"

    // Routes for the bottom navigation, used internally by MainScreen
    const val DASHBOARD = "dashboard"
    const val ANALYTICS = "analytics"
    const val ALERTS = "alerts"
    const val CONTROL = "control"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.MAIN) {
            MainScreen(onNavigateToSettings = {
                navController.navigate(Routes.SETTINGS)
            })
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
    }
}
