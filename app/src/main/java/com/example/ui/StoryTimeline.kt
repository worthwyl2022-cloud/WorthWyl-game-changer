package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.StoryChapter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StoryTimeline(chapters: List<StoryChapter>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(chapters) { chapter ->
            TimelineItem(chapter)
        }
    }
}

@Composable
fun TimelineItem(chapter: StoryChapter) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        // Connector Line
        Surface(
            modifier = Modifier.width(2.dp).heightIn(min = 60.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {}
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Card Content
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = chapter.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(chapter.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = chapter.content, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            }
        }
    }
}
