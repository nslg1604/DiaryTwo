package com.niaz.diary.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "title_id")
    val titleId: Int,

    @ColumnInfo
        (name = "date") val date: String?,

    @ColumnInfo
        (name = "note") val note: String?
){
    constructor(titleId:Int, date: String, note: String) :
            this(0, titleId, date, note)
}
