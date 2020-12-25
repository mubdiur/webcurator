package com.mubdiur.webcurator.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mubdiur.webcurator.databases.daos.ValueDao
import com.mubdiur.webcurator.databases.models.Value

@Database(entities = [Value::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun valueDao(): ValueDao

    companion object{
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context) : MainDatabase{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    MainDatabase::class.java,
                    "appDB")
                    .build()
            }
            return INSTANCE as MainDatabase
        }

        //-------------------------------
    }
}