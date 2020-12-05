package com.mubdiur.webcurator.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Value (
    @PrimaryKey(autoGenerate = true) var uid:Int,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: String
)