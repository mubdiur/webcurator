package io.github.webcurator.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.webcurator.databases.daos.*
import io.github.webcurator.databases.models.*

@Database(
    entities = [Value::class, Site::class, Feed::class, FeedSites::class, SiteQuery::class],
    version = 1
)
abstract class DatabaseClient : RoomDatabase() {


    abstract fun valueDao(): ValueDao
    abstract fun siteDao(): SiteDao
    abstract fun feedDao(): FeedDao
    abstract fun feedSitesDao(): FeedSitesDao
    abstract fun queryDao(): QueryDao

    companion object {
        var INSTANCE: DatabaseClient? = null

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


