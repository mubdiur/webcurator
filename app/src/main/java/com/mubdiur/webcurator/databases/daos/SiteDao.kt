package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.Site


@Dao
interface SiteDao {
    @Query("DELETE from site")
    suspend fun deleteSiteAll(): Int
    @Query("Select * from site where siteId = :siteId limit 1")
    suspend fun getSite(siteId: Long): Site
    @Query("Select * from site where url = :url limit 1")
    suspend fun getSiteByUrl(url: String): Site
    @Query("Select Count(*) from site where url = :url")
    suspend fun countSiteByUrl(url: String): Int
    @Insert
    suspend fun insertSite(site: Site): Long
}