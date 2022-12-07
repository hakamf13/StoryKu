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
    suspend fun getStory(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): AddNewStoryResponse

    @GET("v1/stories")
    fun getStoriesWithLocation(
        @Header("Authorization") Bearer: String,
        @Query("size") size: Int?,
        @Query("location") location: Boolean?
    ): Call<GetAllStoryResponse>

    @GET("v1/stories")
    suspend fun getStoryList(
        @Header("Authorization") Bearer: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): GetAllStoryResponse

}