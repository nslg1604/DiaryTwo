package com.niaz.diary.data.note

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    // Вставка новой записи
    @Insert
    fun insertNote(note: NoteEntity)

//    // Получение всех записей по titleId
//    @Query("SELECT * FROM notes WHERE title_id = :titleId")
//    fun getNotesByTitleId(titleId: Int): List<NoteEntity>

    // Get notes by title
    @Query("SELECT * FROM notes")
    fun getNotes(): MutableList<NoteEntity>

    // Получение всех записей
    @Query("SELECT * FROM notes WHERE title_id = :titleId")
    fun getNotesByTitleId(titleId: Int): MutableList<NoteEntity>

    // Получение записи по titleId и date
    @Query("SELECT * FROM notes WHERE title_id = :titleId AND date = :date")
    fun getNoteByTitleIdAndDate(titleId: Int, date: String): NoteEntity?

    @Query("UPDATE notes SET note = :newNote WHERE title_id = :titleId AND date = :date")
    fun updateNoteByTitleIdAndDate(titleId: Int, date: String, newNote: String)

    // Удаление записи по titleId и date
    @Query("DELETE FROM notes WHERE title_id = :titleId AND date = :date")
    fun deleteNoteByTitleIdAndDate(titleId: Int, date: String)

    // Удаление записи по id
    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteNoteById(id: Int)
}