package com.example.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [StoryChapter::class, MemoryEmbedding::class, BibleEntry::class], version = 3, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun memoryDao(): MemoryDao
    abstract fun bibleDao(): BibleDao

    companion object {
        private const val DATABASE_NAME = "cranium_core.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).build().also { instance = it }
            }
    }
}
