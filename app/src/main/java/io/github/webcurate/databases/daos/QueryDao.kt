package io.github.webcurate.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.webcurate.databases.models.SiteQuery

@Dao
interface QueryDao {
    @Insert
    fun insert(query: SiteQuery): Long

    @Query("Delete from siteQuery where siteId = :siteId")
    fun deleteQueriesBySiteId(siteId: Long)

    @Query("Select * from siteQuery where siteId = :siteId")
    fun getQueriesBySiteId(siteId: Long): List<SiteQuery>

    @Query("Select Count(*) from siteQuery where path = :path and siteId = :siteId")
    fun countByPathSiteId(path: String, siteId: Long): Int

    @Query("Select Count(*) from siteQuery where siteId = :siteId")
    fun countBySiteId(siteId: Long) : Int
}