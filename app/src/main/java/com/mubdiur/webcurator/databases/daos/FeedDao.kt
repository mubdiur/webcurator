package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.Feed
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query("Select * from `feed`")
    fun getAllFeeds(): Flow<MutableList<Feed>>

    @Query("Select * from `feed` where feedId = :feedId limit 1")
    suspend fun getFeed(feedId: Long): Feed

    @Query("Delete from `feed` where feedId = :feedId")
    suspend fun delete(feedId: Long)

    @Query("Delete From `feed`")
    suspend fun deleteAll()

    @Insert
    fun insertFeed(feed: Feed): Long

}