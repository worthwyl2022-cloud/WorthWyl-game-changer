package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.BibleEntry

@Composable
fun BibleScreen(modifier: Modifier = Modifier) {
    // In a real app, this would be backed by a ViewModel
    val entries = remember { mutableStateListOf<BibleEntry>() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("World Building Bible", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(entries) { entry ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(entry.title, style = MaterialTheme.typography.titleMedium)
                        Text(entry.type, style = MaterialTheme.typography.labelSmall)
                        Text(entry.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        
        // Add Button
        FloatingActionButton(
            onClick = { /* TODO: Open Add Entry Dialog */ },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("+")
        }
    }
}
