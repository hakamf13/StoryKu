package com.dicoding.intermediete.submissionstoryapps.ui.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository

class StoryMapsViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getUserToken() = storyRepository.getUserToken().asLiveData()

    fun getStoriesWithLocation(token: String) {
        storyRepository.getStoriesWithLocation(token)
    }

}