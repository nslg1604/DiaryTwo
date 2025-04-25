package com.niaz.diary.data.note

class NoteRepo(private val noteDao: NoteDao) {
    suspend fun insertNote(noteEntity: NoteEntity) {
        noteDao.insertNote(note = noteEntity)
    }

    suspend fun getNotes(): MutableList<NoteEntity>? {
        return noteDao.getNotes()
    }

    suspend fun getNotesByTitleId(titleId: Int): MutableList<NoteEntity>? {
        return noteDao.getNotesByTitleId(titleId)
    }

    suspend fun getNote(titleId: Int, date: String): NoteEntity? {
        return noteDao.getNoteByTitleIdAndDate(titleId, date)
    }

    suspend fun updateNote(noteEntity: NoteEntity) {
        return noteDao.updateNoteByTitleIdAndDate(noteEntity.titleId,
            noteEntity.date!!, noteEntity.note!!)
    }
}