package com.dicoding.intermediete.submissionstoryapps.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun postRegister(name: String, email: String, password: String) =
        storyRepository.postRegister(name, email, password)

}