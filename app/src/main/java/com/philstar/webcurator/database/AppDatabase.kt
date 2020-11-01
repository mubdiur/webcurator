package com.philstar.webcurator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Html::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): HtmlDao

    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context) : AppDatabase{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "appDB")
                    .build()
            }
            return INSTANCE as AppDatabase
        }

        //-------------------------------
    }
}