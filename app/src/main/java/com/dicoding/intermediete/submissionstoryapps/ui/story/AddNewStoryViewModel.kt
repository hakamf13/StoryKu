package com.dicoding.intermediete.submissionstoryapps.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddNewStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getUserToken() = storyRepository.getUserToken()

    fun uploadImage(
        token: String,
        imageMultipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ) {
        storyRepository.uploadImage(token, imageMultipartBody, description, lat, lon)
    }

}