package com.example.randominsect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.randominsect.ui.favorites.FavoritesScreen
import com.example.randominsect.ui.main.MainScreen
import com.example.randominsect.ui.settings.BlackListScreen
import com.example.randominsect.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

// 1. Define Type-Safe Destinations
@Serializable object MainRoute
@Serializable object FavoritesRoute
@Serializable object SettingsRoute
@Serializable object BlacklistRoute

// 2. Set Up Navigation Graph
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute
    ) {
        composable<MainRoute> {
            MainScreen(
                onNavigateToFavorites = { navController.navigate(FavoritesRoute) },
                onNavigateToSettings = { navController.navigate(SettingsRoute) }
            )
        }

        composable<FavoritesRoute> {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBlacklist = { navController.navigate(BlacklistRoute) }
            )
        }

        composable<BlacklistRoute> {
            BlackListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
