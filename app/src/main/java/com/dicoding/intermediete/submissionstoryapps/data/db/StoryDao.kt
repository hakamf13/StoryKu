package com.dicoding.intermediete.submissionstoryapps.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStories(stories: List<StoryModel>)

    @Query("SELECT * FROM story ORDER BY createdAt DESC")
    fun getStories(): PagingSource<Int, StoryModel>

    @Query("SELECT * FROM story WHERE id = :id LIMIT 1")
    fun getDetailStory(id: String): LiveData<StoryModel>

    @Query("DELETE FROM story")
    suspend fun deleteAll()

}