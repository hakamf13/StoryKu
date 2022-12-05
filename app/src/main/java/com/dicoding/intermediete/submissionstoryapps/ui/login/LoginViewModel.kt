package com.dicoding.intermediete.submissionstoryapps.ui.login

import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import kotlinx.coroutines.*

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getUserToken(): LiveData<UserModel> {
        return pref.getUserToken().asLiveData()
    }

    fun userLogin() {
        viewModelScope.launch {
            pref.userLogin()
        }
    }

    fun userSave(user: LoginResult) {
        viewModelScope.launch {
            pref.userSave(user)
        }
    }
}