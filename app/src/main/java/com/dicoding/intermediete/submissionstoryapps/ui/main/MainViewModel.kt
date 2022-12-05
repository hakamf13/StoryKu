package com.dicoding.intermediete.submissionstoryapps.ui.main

import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import kotlinx.coroutines.*

class MainViewModel(private val pref: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getUserToken(): LiveData<UserModel> {
        return pref.getUserToken().asLiveData()
    }

    fun userLogout() {
        viewModelScope.launch {
            pref.userLogout()
        }
    }
}