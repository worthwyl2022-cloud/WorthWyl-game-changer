package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_embeddings ORDER BY timestamp DESC")
    suspend fun getAllMemories(): List<MemoryEmbedding>

    @Insert
    suspend fun insertMemory(memory: MemoryEmbedding)
}
