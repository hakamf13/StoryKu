package com.dicoding.intermediete.submissionstoryapps.data.remote.network

import com.dicoding.intermediete.submissionstoryapps.data.remote.response.AddNewStoryResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.GetAllStoryResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.LoginResponse
import com.dicoding.intermediete.submissionstoryapps.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("v1/login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("v1/stories")
    fun getStory(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddNewStoryResponse>

    @GET("v1/stories")
    fun getStoryList(
        @Header("Authorization") Bearer: String
    ): Call<GetAllStoryResponse>

}