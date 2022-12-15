package com.dicoding.intermediete.submissionstoryapps.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.intermediete.submissionstoryapps.data.local.RemoteKeys
import com.dicoding.intermediete.submissionstoryapps.data.local.StoryModel

@Database(
    entities = [StoryModel::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: StoryDatabase? = null

        fun getInstance(context: Context): StoryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java,
                    "storyDB.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}