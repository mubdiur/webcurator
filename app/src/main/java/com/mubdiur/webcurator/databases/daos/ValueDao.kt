package com.mubdiur.webcurator.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mubdiur.webcurator.databases.models.Value


@Dao
interface ValueDao {
    @Query("SELECT value from value where `key` = :key limit 1")
    fun getValue(key:String): String
    @Query("DELETE from value where `key` = :key")
    fun deleteValue(key:String): Int
    @Query("DELETE from value")
    fun deleteValueAll(): Int
    @Insert
    fun insertValue(value: Value)
}