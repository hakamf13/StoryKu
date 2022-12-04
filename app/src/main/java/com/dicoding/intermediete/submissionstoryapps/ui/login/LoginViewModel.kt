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
            val client = ApiConfig.getApiService().userLogin(email, password)
            withContext(Dispatchers.Main) {
                client.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                saveUserToken(responseBody.loginResult.token)
                                Log.e(TAG, "onSuccess: ${responseBody.message}")
                            } else {
                                saveUserToken(responseBody!!.loginResult.token)
                                Log.d("ERROR_LOGIN", "onFailure: ${responseBody.message}")
                            }
                        } else {
                            Log.d("ERROR_LOGIN", "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e("ERROR_LOGIN", "onFailure: ${t.message.toString()}")
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