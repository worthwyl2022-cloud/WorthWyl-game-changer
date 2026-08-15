package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bible_entries")
data class BibleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // CHARACTER, LORE, TIMELINE, SETTING, HISTORY
    val title: String,
    val content: String
)
