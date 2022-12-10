package com.dicoding.intermediete.submissionstoryapps.ui.story

import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository

class StoryDetailViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getDetailStories(id: String) = storyRepository.getDetailStories(id)

}