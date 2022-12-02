package com.dicoding.intermediete.submissionstoryapps.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResult
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    companion object {
        const val TAG = "RegisterViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var job: Job ?= null


    fun register(user: RegisterResult) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val service = ApiConfig.getApiService().userRegister(user.name, user.email, user.password)
            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<RegisterResponse> {

                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            if (response.body() != null && response.body()!!.error) {
                                Log.e(TAG, "onFailure: ${response.message()}")
                            }
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e(TAG, "onFailure: ${t.message.toString()}")
                    }
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}