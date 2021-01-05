package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.Site


@Dao
interface SiteDao {
    @Query("SELECT * from site")
    suspend fun getAllSites(): List<Site>
    @Query("DELETE from site")
    suspend fun deleteSiteAll(): Int
    @Query("Select * from site where siteId = :siteId limit 1")
    suspend fun getSite(siteId: Long): Site
    @Query("Select Count(*) from site where url = :url and queries = :queries")
    suspend fun getCount(url: String, queries: String): Int
    @Query("Select siteId from site where url = :url and queries = :queries limit 1")
    suspend fun getId(url: String, queries: String): Long
    @Insert
    suspend fun insertSite(site: Site): Long
}