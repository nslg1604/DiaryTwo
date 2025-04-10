package com.niaz.diary.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niaz.diary.utils.MyData

@Entity(tableName = "titles")
data class TitleEntity(
    @PrimaryKey(autoGenerate = true)
//    @PrimaryKey
    val id: Int,

    @ColumnInfo
        (name = "title") val title: String
)
{
    constructor(title: String) : this(0, title)
//    constructor(title: String) : this(++MyData.nextId, title)
}