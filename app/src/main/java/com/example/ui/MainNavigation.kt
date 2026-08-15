package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavigation(
    navController: NavHostController,
    viewModel: StoryViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Forge.route) { ForgeScreen() }
        composable(Screen.Writer.route) { 
            WriterWorkspace(onThink = { text -> viewModel.think("default_pipeline", text) }) 
        }
        composable(Screen.Tracker.route) { TrackerScreen() }
        composable(Screen.Bible.route) { BibleScreen() }
        composable(Screen.StyleSync.route) { StyleSyncScreen(viewModel) }
    }
}
