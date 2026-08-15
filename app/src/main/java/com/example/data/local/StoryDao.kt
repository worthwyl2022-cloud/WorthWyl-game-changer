package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM story_chapters ORDER BY timestamp ASC")
    fun getAllChapters(): Flow<List<StoryChapter>>

    @Query("SELECT * FROM story_chapters ORDER BY timestamp ASC")
    suspend fun getAllChaptersList(): List<StoryChapter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: StoryChapter)
}
