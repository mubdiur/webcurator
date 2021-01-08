package com.mubdiur.webcurator.databases.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed")
data class Feed (
    @ColumnInfo(name = "title") var feedTitle: String,
    @ColumnInfo(name = "description") var feedDescription: String
    ) {
    @PrimaryKey(autoGenerate = true) var feedId: Long = 0
}