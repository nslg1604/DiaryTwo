package com.niaz.diary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niaz.diary.data.note.NoteDao
import com.niaz.diary.data.note.NoteEntity
import com.niaz.diary.data.title.TitleDao
import com.niaz.diary.data.title.TitleEntity

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        TitleEntity::class,
        NoteEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun titleDao(): TitleDao
    abstract fun noteDao(): NoteDao
}
