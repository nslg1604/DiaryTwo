package com.niaz.diary.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TitleEntity::class,
        NoteEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun titleDao(): TitleDao
    abstract fun noteDao(): NoteDao
}
