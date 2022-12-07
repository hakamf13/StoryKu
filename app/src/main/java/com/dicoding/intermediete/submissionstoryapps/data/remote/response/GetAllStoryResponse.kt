package com.dicoding.intermediete.submissionstoryapps.data.remote.response

import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel
import com.google.gson.annotations.SerializedName

data class GetAllStoryResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryModel>?

)