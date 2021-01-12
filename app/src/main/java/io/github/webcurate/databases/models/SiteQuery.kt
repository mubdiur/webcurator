package io.github.webcurate.databases.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "siteQuery")
data class SiteQuery(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "siteId") val siteId: Long,
    @PrimaryKey(autoGenerate = true) val siteQueryId: Long = 0
)