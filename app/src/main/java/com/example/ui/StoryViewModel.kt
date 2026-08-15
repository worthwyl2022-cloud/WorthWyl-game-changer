package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.StoryChapter
import com.example.data.repository.StoryLocalRepository
import com.worthwyl.android.ai.AIOrchestrator
import com.worthwyl.android.ai.StoryPersonality
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StoryViewModel(private val repository: StoryLocalRepository) : ViewModel() {
    val chapters: StateFlow<List<StoryChapter>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    private val _personality = MutableStateFlow(StoryPersonality("Neutral", "Standard", "Descriptive"))
    val personality: StateFlow<StoryPersonality> = _personality
    
    private val _driftAlert = MutableStateFlow<String?>(null)
    val driftAlert: StateFlow<String?> = _driftAlert
    
    private val orchestrator = AIOrchestrator(repository)

    fun updatePersonality(newPersonality: StoryPersonality) {
        _personality.value = newPersonality
    }

    fun think(pipelineId: String, prompt: String) {
        viewModelScope.launch {
            orchestrator.streamResponse(pipelineId, prompt, _personality.value).collect { event ->
                if (event.startsWith("Drift: ")) {
                    _driftAlert.value = event.substringAfter("Drift: ")
                }
            }
        }
    }
    
    fun clearDriftAlert() {
        _driftAlert.value = null
    }
}

class StoryViewModelFactory(private val repository: StoryLocalRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
