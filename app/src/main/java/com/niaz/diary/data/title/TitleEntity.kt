package com.niaz.diary.data.title

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "titles")
data class TitleEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo
        (name = "title") val title: String
)
{
    constructor(title: String) : this(0, title)
}