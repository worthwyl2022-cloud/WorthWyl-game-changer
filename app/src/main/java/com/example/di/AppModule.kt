package com.example.di

import android.content.Context
import com.example.BuildConfig
import com.example.core.substrate.SubstrateCore
import com.example.data.local.AppDatabase
import com.example.data.local.StoryDao
import com.example.data.repository.StoryLocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSubstrateCore(): SubstrateCore {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            "MOCK_KEY"
        }
        return SubstrateCore(apiKey)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideStoryDao(database: AppDatabase): StoryDao {
        return database.storyDao()
    }

    @Provides
    @Singleton
    fun provideStoryLocalRepository(storyDao: StoryDao): StoryLocalRepository {
        return StoryLocalRepository(storyDao)
    }
}
