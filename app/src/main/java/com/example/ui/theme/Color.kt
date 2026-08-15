package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val DeepBackground = Color(0xFF0F0F12) // Slightly off-black
val RichGold = Color(0xFFD4AF37)      // Metallic gold
val FieryOrange = Color(0xFFFF5722)   // Vibrant fire orange
val SurfaceColor = Color(0xFF1C1C21)  // Dark gray surface

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = RichGold,
    secondary = FieryOrange,
    background = DeepBackground,
    surface = SurfaceColor,
    onPrimary = DeepBackground,
    onBackground = Color.White,
    onSurface = Color.White
)
