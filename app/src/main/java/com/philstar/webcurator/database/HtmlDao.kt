package com.philstar.webcurator.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface HtmlDao {
    @Query("SELECT * from html")
    fun getAll(): List<Html>
    @Query("DELETE from html")
    fun deleteAll(): Int
    @Insert
    fun insert(user: Html)

}