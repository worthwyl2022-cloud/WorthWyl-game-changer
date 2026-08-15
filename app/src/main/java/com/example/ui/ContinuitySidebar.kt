package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ContinuitySidebar(modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxHeight().width(250.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Continuity Info", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Characters:", style = MaterialTheme.typography.titleMedium)
            // TODO: Implement actual data mapping
            Text("• Protagonist", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Open Threads:", style = MaterialTheme.typography.titleMedium)
            Text("• The Lost Key", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
