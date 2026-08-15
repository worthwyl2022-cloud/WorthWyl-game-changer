package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.worthwyl.android.ai.StoryPersonality

@Composable
fun PersonalityConfigScreen(
    currentPersonality: StoryPersonality,
    onSave: (StoryPersonality) -> Unit
) {
    var tone by remember { mutableStateOf(currentPersonality.tone) }
    var vocabulary by remember { mutableStateOf(currentPersonality.vocabulary) }
    var narrativeStyle by remember { mutableStateOf(currentPersonality.narrativeStyle) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Story Personality", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(value = tone, onValueChange = { tone = it }, label = { Text("Tone") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = vocabulary, onValueChange = { vocabulary = it }, label = { Text("Vocabulary") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = narrativeStyle, onValueChange = { narrativeStyle = it }, label = { Text("Narrative Style") }, modifier = Modifier.fillMaxWidth())
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSave(StoryPersonality(tone, vocabulary, narrativeStyle)) }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Personality")
        }
    }
}
