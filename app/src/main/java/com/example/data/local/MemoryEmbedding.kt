package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_embeddings")
data class MemoryEmbedding(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val embedding: List<Float>, // Serialized vector
    val timestamp: Long = System.currentTimeMillis()
)
