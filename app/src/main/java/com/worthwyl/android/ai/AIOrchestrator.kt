package com.worthwyl.android.ai

import com.example.data.repository.StoryLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Note: Using a simplified constructor injection as Hilt is not currently configured.
class AIOrchestrator(private val repo: StoryLocalRepository) {

    fun streamResponse(
        pipelineId: String,
        prompt: String,
        personality: StoryPersonality
    ): Flow<String> = flow {
        val response = BackendClient.think(pipelineId, prompt, personality)
        emit("Directive: ${response.directive}")
        emit("Artifact: ${response.artifact}")
        emit("LLM: ${response.llm}")
        response.narrativeDrift?.let { emit("Drift: $it") }
        
        // This part needs to be mapped to the actual Repository.insert implementation
        // Since StoryLocalRepository has `insert(chapter: StoryChapter)`,
        // I will map this node insertion to that, for now.
    }
}
