package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrackerScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Narrative Intelligence Tracker", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Metacognitive dashboard: tension, continuity meters
        LinearProgressIndicator(progress = 0.7f, modifier = Modifier.fillMaxWidth())
        Text("Tension Meter: 70%", style = MaterialTheme.typography.bodyMedium)
    }
}
