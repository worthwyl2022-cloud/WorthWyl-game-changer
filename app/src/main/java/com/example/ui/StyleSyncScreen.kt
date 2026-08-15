package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.core.style.StyleAnalyzer
import com.worthwyl.android.ai.StoryPersonality
import kotlinx.coroutines.launch

@Composable
fun StyleSyncScreen(viewModel: StoryViewModel, modifier: Modifier = Modifier) {
    var sampleText by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ready") }
    val scope = rememberCoroutineScope()
    val analyzer = remember { StyleAnalyzer() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Style Sync Wizard", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = sampleText,
            onValueChange = { sampleText = it },
            label = { Text("Paste your sample writing here") },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scope.launch {
                    status = "Analyzing..."
                    val styleSummary = analyzer.analyzeStyle(sampleText)
                    viewModel.updatePersonality(StoryPersonality("Custom", styleSummary, styleSummary))
                    status = "Style Synced!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sync Writing Style")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(status, style = MaterialTheme.typography.bodySmall)
    }
}
