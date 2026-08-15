package com.example.core.style

import com.example.BuildConfig
import com.example.core.extraction.Content
import com.example.core.extraction.GenerateContentRequest
import com.example.core.extraction.Part
import com.example.core.extraction.RetrofitClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Service to analyze sample text and extract a writing style profile.
 */
class StyleAnalyzer {

    suspend fun analyzeStyle(sampleText: String): String {
        val prompt = """
            Analyze the following writing sample and extract the unique writing voice (vocabulary, sentence structure, tone).
            Return a concise summary of the writing style that can be used to inform an AI generation personality.
            
            Sample Text:
            $sampleText
        """.trimIndent()
        
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )
        
        return try {
            val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Standard, neutral style."
        } catch (e: Exception) {
            "Standard, neutral style."
        }
    }
}
