package com.dicoding.intermediete.submissionstoryapps.ui.login

import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository
import kotlinx.coroutines.*

class LoginViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun postLogin(email: String, password: String) =
        storyRepository.postLogin(email, password)

    fun userSave(user: LoginResult) {
        viewModelScope.launch {
            storyRepository.userSave(user)
        }
    }
}