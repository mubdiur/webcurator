package com.mubdiur.webcurator.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.database.model.Html


@Dao
interface HtmlDao {
    @Query("SELECT * from html limit 1")
    fun getHtml(): Html
    @Query("DELETE from html")
    fun deleteHtml(): Int
    @Insert
    fun insertHtml(html: Html)

}