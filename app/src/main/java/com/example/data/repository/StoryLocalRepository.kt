package com.example.data.repository

import com.example.data.local.StoryChapter
import com.example.data.local.StoryDao
import kotlinx.coroutines.flow.Flow

class StoryLocalRepository(private val storyDao: StoryDao) {
    val allChapters: Flow<List<StoryChapter>> = storyDao.getAllChapters()

    suspend fun insert(chapter: StoryChapter) = storyDao.insertChapter(chapter)

    suspend fun exportStoryThread(): String {
        val chapters = storyDao.getAllChaptersList()
        return buildString {
            appendLine("--- WorthWyl Story Thread Export ---")
            chapters.forEach { chapter ->
                appendLine("Title: ${chapter.title}")
                appendLine("Timestamp: ${java.util.Date(chapter.timestamp)}")
                appendLine("Content:")
                appendLine(chapter.content)
                appendLine("---")
                appendLine()
            }
        }
    }
}
