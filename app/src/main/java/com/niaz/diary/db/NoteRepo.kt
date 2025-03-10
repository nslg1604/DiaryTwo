package com.niaz.diary.db

class NoteRepo(private val noteDao: NoteDao) {
    suspend fun insertNote(noteEntity: NoteEntity) {
        noteDao.insertNote(note = noteEntity)
    }

    suspend fun getNotes(): MutableList<NoteEntity>? {
        return noteDao.getNotes()
    }

    suspend fun getNote(titleId: Int, date: String): NoteEntity? {
        return noteDao.getNoteByTitleIdAndDate(titleId, date)
    }

    suspend fun updateNote(titleId: Int, date: String, note:String) {
        return noteDao.updateNoteByTitleIdAndDate(titleId, date, note)
    }
}