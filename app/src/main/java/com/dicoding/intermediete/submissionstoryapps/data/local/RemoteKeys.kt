package com.dicoding.intermediete.submissionstoryapps.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(

    @PrimaryKey
    val id: String,
    val prevKey: Int?,
    val nextKey: Int?

)