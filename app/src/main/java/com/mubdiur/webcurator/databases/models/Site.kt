package com.mubdiur.webcurator.databases.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(primaryKeys = ["url", "queries"])
data class Site(
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "queries") val queries: List<String>
)

