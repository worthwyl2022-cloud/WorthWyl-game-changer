package com.example.core.extraction

import com.example.BuildConfig
import com.example.data.local.BibleDao
import com.example.data.local.BibleEntry
import com.example.data.local.StoryChapter
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 * Service to extract lore, character, and setting updates from story chapters.
 */
class BibleExtractionService(private val bibleDao: BibleDao) {

    // Using Gemini API for extraction
    suspend fun extractAndSuggestUpdates(chapter: StoryChapter) {
        val prompt = """
            Analyze the following story chapter and extract any new characters, lore, timelines, or setting rules.
            Format the response as a JSON array of objects, where each object has 'type' (CHARACTER, LORE, TIMELINE, SETTING, HISTORY), 'title', and 'content'.
            
            Chapter Title: ${chapter.title}
            Content: ${chapter.content}
        """.trimIndent()
        
        val suggestedEntries = callGeminiForExtraction(prompt)
        
        suggestedEntries.forEach { entry ->
            bibleDao.insertEntry(entry)
        }
    }

    private suspend fun callGeminiForExtraction(prompt: String): List<BibleEntry> {
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        
        return try {
            val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            val text = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: return emptyList()
            
            // Basic parsing of the JSON array
            val jsonArray = Json.parseToJsonElement(text).jsonArray
            jsonArray.map { element ->
                val obj = element.jsonObject
                BibleEntry(
                    type = obj["type"]?.jsonPrimitive?.content ?: "LORE",
                    title = obj["title"]?.jsonPrimitive?.content ?: "New Entry",
                    content = obj["content"]?.jsonPrimitive?.content ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
