package com.dicoding.intermediete.submissionstoryapps.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import kotlinx.coroutines.*

class MainViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getUserToken() = storyRepository.getUserToken().asLiveData()

    fun userLogout() {
        viewModelScope.launch {
            storyRepository.userLogout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<StoryModel>> =
        storyRepository.getStories(token).cachedIn(viewModelScope)

}