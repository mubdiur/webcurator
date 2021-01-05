package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.FeedSites

@Dao
interface FeedSitesDao {
    @Insert
    suspend fun insert(feedSites: FeedSites)

    @Query("Select `siteId` from `feedSites` where feedId = :feedId")
    suspend fun getSitesByFeedId(feedId: Long): List<Long>

    @Query("Delete from `feedSites` where feedId = :feedId")
    suspend fun deleteSitesByFeedId(feedId: Long)
}