package com.example.ai

import com.example.BuildConfig
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

object BackendClient {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
    }



    private val BASE_URL = BuildConfig.BACKEND_URL.ifEmpty { "http://10.0.2.2:8000" }

    suspend fun think(pipelineId: String, text: String, personality: StoryPersonality? = null): ThinkResponse {
        return client.post("$BASE_URL/ai/think") {
            setBody(ThinkRequest(pipelineId, text, personality))
        }.body()
    }
}

@Serializable
data class StoryPersonality(
    val tone: String,
    val vocabulary: String,
    val narrativeStyle: String
)

@Serializable
data class ThinkRequest(
    val pipelineId: String, 
    val text: String,
    val personality: StoryPersonality? = null
)

@Serializable
data class ThinkResponse(
    val artifact: String,
    val directive: String,
    val llm: String,
    val narrativeDrift: String? = null
)
