package com.dicoding.intermediete.submissionstoryapps.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResponse
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference): ViewModel() {

    companion object {
        const val TAG = "LoginViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var job: Job ?= null


    fun login(email: String, password: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val service = ApiConfig.getApiService().userLogin(email, password)
            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            if (response.body() != null && response.body()!!.error) {
                                saveUserToken(response.body()!!.loginResult.token)
                                Log.e(TAG, "onFailure: ${response.message()}")
                            }
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e(TAG, "onFailure: ${t.message.toString()}")
                    }
                })
            }
        }
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            pref.userLogin(token)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}