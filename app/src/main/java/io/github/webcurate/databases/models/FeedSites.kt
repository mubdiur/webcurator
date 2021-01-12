package io.github.webcurate.databases.models

import androidx.room.*

@Entity(tableName = "feedSites")
data class FeedSites (
    @ColumnInfo(name = "feedId") val feedId: Long,
    @ColumnInfo(name = "siteId") val siteId: Long
        ) {
    @PrimaryKey(autoGenerate = true) var id:Long = 0
}