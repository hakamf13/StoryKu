package com.dicoding.intermediete.submissionstoryapps.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddNewStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getUserToken() = storyRepository.getUserToken().asLiveData()

    fun uploadImage(
        token: String,
        imageMultipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = storyRepository.uploadImageStory(token, imageMultipartBody, description, lat, lon)

}