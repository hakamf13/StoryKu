package com.dicoding.intermediete.submissionstoryapps.ui.register

import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun postRegister(name: String, email: String, password: String) =
        storyRepository.postRegister(name, email, password)

}