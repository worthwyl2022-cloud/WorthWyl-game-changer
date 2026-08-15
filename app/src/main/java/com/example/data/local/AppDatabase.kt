package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [StoryChapter::class, MemoryEmbedding::class, BibleEntry::class], version = 3, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun memoryDao(): MemoryDao
    abstract fun bibleDao(): BibleDao
}
