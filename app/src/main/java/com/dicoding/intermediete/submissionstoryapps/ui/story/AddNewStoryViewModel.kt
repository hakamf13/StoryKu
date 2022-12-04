package com.dicoding.intermediete.submissionstoryapps.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.intermediete.submissionstoryapps.data.local.User
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.AddNewStoryResponse
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewStoryViewModel(private val pref: UserPreference): ViewModel() {

    companion object {
        const val TAG = "AddNewStoryViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    fun getUserToken(): LiveData<User> {
        return pref.getUserToken().asLiveData()
    }

    fun uploadImage(file: MultipartBody.Part, description: RequestBody, token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStory(file, description, "Bearer $token")
        client.enqueue(object : Callback<AddNewStoryResponse> {
            override fun onResponse(
                call: Call<AddNewStoryResponse>,
                response: Response<AddNewStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Log.e(TAG, "onSuccess GetStories: ${responseBody.message}")
                    } else {
                        Log.d("ERROR_ADD_STORIES", "onFailure: ${responseBody!!.message}")
                    }
                } else {
                    Log.d("ERROR_ADD_STORIES", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                Log.e("ERROR_ADD_STORIES", "onFailure: ${t.message.toString()}")
            }
        })
    }
}