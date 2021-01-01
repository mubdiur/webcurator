package com.mubdiur.webcurator.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mubdiur.webcurator.databases.daos.SiteDao
import com.mubdiur.webcurator.databases.daos.ValueDao
import com.mubdiur.webcurator.databases.models.Site
import com.mubdiur.webcurator.databases.models.Value

@Database(entities = [Value::class, Site::class], version = 1)
@TypeConverters(StringListConverters::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun valueDao(): ValueDao
    abstract fun siteDao(): SiteDao

    companion object {
        private var INSTANCE: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    MainDatabase::class.java,
                    "appDB"
                )
                    .build()
            }
            return INSTANCE as MainDatabase
        }

        //-------------------------------
    }
}