package io.github.webcurate.databases.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.webcurate.databases.models.Value


@Dao
interface ValueDao {
    @Query("SELECT value from value where `key` = :key limit 1")
    suspend fun getValue(key:String): String
    @Query("DELETE from value where `key` = :key")
    suspend fun deleteValue(key:String): Int
    @Query("DELETE from value")
    suspend fun deleteValueAll(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValue(value: Value)
}