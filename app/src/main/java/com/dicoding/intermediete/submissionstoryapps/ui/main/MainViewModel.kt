package com.dicoding.intermediete.submissionstoryapps.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.intermediete.submissionstoryapps.data.local.User
import com.dicoding.intermediete.submissionstoryapps.data.local.UserPreference
import com.dicoding.intermediete.submissionstoryapps.data.remote.network.ApiConfig
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.GetAllStoryResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.ListStory
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference): ViewModel() {

    companion object{
        const val TAG = "MainViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val storyList = MutableLiveData<List<ListStory>>()

    private var job: Job ?= null


    fun getUserToken(): LiveData<User> {
        return pref.getUserToken().asLiveData()
    }

    fun setUserData(token: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            _isLoading.value = true
            val client = ApiConfig.getApiService().getStoryList("Bearer $token")
            withContext(Dispatchers.Main) {
                client.enqueue(object : Callback<GetAllStoryResponse> {
                    override fun onResponse(call: Call<GetAllStoryResponse>, response: Response<GetAllStoryResponse>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                storyList.postValue(responseBody.listStory)
                                Log.e(TAG, "onSuccess: ${responseBody.message}")
                            } else {
                                Log.d("ERROR", "onFailure: ${responseBody!!.message}")
                            }
                        } else {
                            Log.d("ERROR", "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e("ERROR", "onFailure: ${t.message.toString()}")
                    }
                })
            }
        }
    }

    fun userLogout() {
        viewModelScope.launch {
            pref.userLogout()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}