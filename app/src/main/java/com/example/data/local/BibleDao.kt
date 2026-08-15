package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM bible_entries")
    fun getAllEntries(): Flow<List<BibleEntry>>

    @Insert
    suspend fun insertEntry(entry: BibleEntry)
}
