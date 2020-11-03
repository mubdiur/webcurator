package com.mubdiur.webcurator.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Html (
    @PrimaryKey(autoGenerate = true) var uid:Int,
    @ColumnInfo(name = "htmlText") val htmlText: String
)