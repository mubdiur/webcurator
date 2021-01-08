package com.mubdiur.webcurator.databases.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "site")
data class Site(
    @ColumnInfo(name = "url") val url: String
) {
    @PrimaryKey(autoGenerate = true) var siteId: Long = 0
}

