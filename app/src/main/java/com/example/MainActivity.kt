package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.core.substrate.SubstrateCore
import com.example.ui.SubstrateTerminal
import com.example.ui.DeepBg

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize the cognitive substrate engine
        // Assuming API key is in BuildConfig or we fall back to a mock string if not present
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            "MOCK_KEY"
        }
        val substrateCore = SubstrateCore(apiKey)
        
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = DeepBg) {
                SubstrateTerminal(core = substrateCore)
            }
        }
    }
}
