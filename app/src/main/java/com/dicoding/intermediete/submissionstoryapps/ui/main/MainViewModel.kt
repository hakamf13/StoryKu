package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.GetAllStoryResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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