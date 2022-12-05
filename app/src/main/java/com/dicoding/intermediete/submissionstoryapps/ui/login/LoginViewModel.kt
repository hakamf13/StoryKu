package com.dicoding.intermediete.submissionstoryapps.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.local.UserModel
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResult
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        userLogin()
                        userSave(responseBody.loginResult)
                        Log.d("LOGIN", "onSuccess: ${responseBody.message}")
                    } else {
                        Log.e("LOGIN_ERROR", "onError: ${responseBody.message}")
                    }
                } else {
                    Log.e("LOGIN_ERROR", "onError: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("LOGIN_ERROR", "onError: ${t.message.toString()}")
            }
        })
    }

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