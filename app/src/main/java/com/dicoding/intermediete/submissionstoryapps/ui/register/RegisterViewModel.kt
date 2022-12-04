package com.dicoding.intermediete.submissionstoryapps.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResponse
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


    fun register(name: String, email: String, password: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
//            _isLoading.value = true
            val client = ApiConfig.getApiService().userRegister(name, email, password)
            withContext(Dispatchers.Main) {
                client.enqueue(object : Callback<RegisterResponse> {
                    override fun onResponse(
                        call: Call<RegisterResponse>,
                        response: Response<RegisterResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Log.e(TAG, "onSuccess: ${responseBody.message}")
                            } else {
                                Log.d("ERROR_REGISTER", "onFailure: ${responseBody!!.message}")
                            }
                        } else {
                            Log.d("ERROR_REGISTER", "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
//                        _isLoading.value = false
                        Log.e("ERROR_REGISTER", "onFailure: ${t.message.toString()}")
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