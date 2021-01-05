package com.mubdiur.webcurator.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mubdiur.webcurator.databases.daos.FeedDao
import com.mubdiur.webcurator.databases.daos.FeedSitesDao
import com.mubdiur.webcurator.databases.daos.SiteDao
import com.mubdiur.webcurator.databases.daos.ValueDao
import com.mubdiur.webcurator.databases.models.Feed
import com.mubdiur.webcurator.databases.models.FeedSites
import com.mubdiur.webcurator.databases.models.Site
import com.mubdiur.webcurator.databases.models.Value

@Database(entities = [Value::class, Site::class, Feed::class, FeedSites::class], version = 1)
abstract class DatabaseClient : RoomDatabase() {


    abstract fun valueDao(): ValueDao
    abstract fun siteDao(): SiteDao
    abstract fun feedDao(): FeedDao
    abstract fun feedSitesDao(): FeedSitesDao

    companion object {
        private var INSTANCE: DatabaseClient? = null

        fun getInstance(context: Context): DatabaseClient {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    DatabaseClient::class.java,
                    "appDB"
                )
                    .build()
            }
            return INSTANCE as DatabaseClient
        }

        //-------------------------------
    }
}