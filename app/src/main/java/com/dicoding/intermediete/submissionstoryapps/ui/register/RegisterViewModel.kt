package com.dicoding.intermediete.submissionstoryapps.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun register(user: RegisterResult) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().postRegister(
            user.name,
            user.email,
            user.password
        )
        client.enqueue(object : Callback<RegisterResponse> {

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        Log.d("REGISTER", "onSuccess: ${responseBody.message}")
                    } else {
                        Log.e("REGISTER_ERROR", "onError: ${responseBody.message}")
                    }
                } else {
                    Log.e("REGISTER_ERROR", "onError: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.d("REGISTER_ERROR", "onError: ${t.message.toString()}")
            }
        })
    }
}