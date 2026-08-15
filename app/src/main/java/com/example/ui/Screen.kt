package com.example.ui

sealed class Screen(val route: String, val title: String) {
    object Forge : Screen("forge", "Forge")
    object Writer : Screen("writer", "Writer")
    object Tracker : Screen("tracker", "Tracker")
    object Bible : Screen("bible", "Bible")
    object StyleSync : Screen("stylesync", "Style Sync")
    object Splash : Screen("splash", "Splash")
}
