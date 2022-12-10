package com.dicoding.intermediete.submissionstoryapps.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.intermediete.submissionstoryapps.data.di.Injection
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import com.dicoding.intermediete.submissionstoryapps.ui.login.LoginViewModel
import com.dicoding.intermediete.submissionstoryapps.ui.main.MainViewModel
import com.dicoding.intermediete.submissionstoryapps.ui.maps.StoryMapsViewModel
import com.dicoding.intermediete.submissionstoryapps.ui.register.RegisterViewModel
import com.dicoding.intermediete.submissionstoryapps.ui.story.AddNewStoryViewModel
import com.dicoding.intermediete.submissionstoryapps.ui.story.StoryDetailViewModel

class ViewModelFactory private constructor(private val storyRepository: StoryRepository)
    : ViewModelProvider.NewInstanceFactory() {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideRepository(context, context.dataStore)
                )
            }.also {
                INSTANCE = it
            }
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(AddNewStoryViewModel::class.java) -> {
                AddNewStoryViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(storyRepository) as T
            }

            modelClass.isAssignableFrom(StoryMapsViewModel::class.java) -> {
                StoryMapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

}