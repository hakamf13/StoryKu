package com.dicoding.intermediete.submissionstoryapps.data.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryModel(

    val id: String,

    val name: String,

    val description: String,

    val photoUrl: String,

    val createdAt: String,

    val lat: Float?,

    val lon: Float?

) : Parcelable