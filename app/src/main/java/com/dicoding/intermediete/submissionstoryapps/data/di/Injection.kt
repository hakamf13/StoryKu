package com.dicoding.intermediete.submissionstoryapps.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dicoding.intermediete.submissionstoryapps.data.db.StoryDatabase
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository

object Injection {
    fun provideRepository(
        context: Context,
        dataStore: DataStore<Preferences>
    ): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getInstance(context)
        val storyDao = storyDatabase.storyDao()
        val userPreference = UserPreference.getInstance(dataStore)

        return StoryRepository.getInstance(apiService, storyDao, storyDatabase, userPreference)
    }
}