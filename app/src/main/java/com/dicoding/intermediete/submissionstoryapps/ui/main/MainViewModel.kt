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

    private val _storyList = MutableLiveData<List<ListStory>>()
    val storyList: LiveData<List<ListStory>> = _storyList

    private var job: Job ?= null

    val errorMessage = MutableLiveData<String>()

    fun getUserToken(): LiveData<User> {
        return pref.getUserToken().asLiveData()
    }

    fun setUserData(token: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val service = ApiConfig.getApiService().getStoryList("Authorization $token")
            withContext(Dispatchers.Main) {
                service.enqueue(object : Callback<GetAllStoryResponse> {

                    override fun onResponse(
                        call: Call<GetAllStoryResponse>,
                        response: Response<GetAllStoryResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            if (response.body() != null && response.body()!!.error) {
                                _storyList.postValue(response.body()!!.listStory)
                                Log.e(TAG, "onFailure: ${response.message()}")
                            }
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                        _isLoading.value = false
                        Log.e(TAG, "onFailure: ${t.message.toString()}")
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