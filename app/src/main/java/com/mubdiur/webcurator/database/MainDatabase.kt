package com.mubdiur.webcurator.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mubdiur.webcurator.database.dao.HtmlDao
import com.mubdiur.webcurator.database.model.Html

@Database(entities = [Html::class], version = 1)
abstract class MainDatabase : RoomDatabase() {
    abstract fun userDao(): HtmlDao

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