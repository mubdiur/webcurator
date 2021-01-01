package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.Site


@Dao
interface SiteDao {
    @Query("SELECT * from site")
    fun getAllSites(): List<Site>
    @Query("DELETE from site")
    fun deleteSiteAll(): Int
    @Insert
    fun insertSite(site: Site)
}