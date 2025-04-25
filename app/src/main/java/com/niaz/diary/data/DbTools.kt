package com.niaz.diary.data

import com.niaz.diary.MyApp
import com.niaz.diary.data.note.NoteEntity
import com.niaz.diary.data.note.NoteRepo
import com.niaz.diary.utils.MyLogger
import javax.inject.Inject

class DbTools @Inject constructor(){
    suspend fun saveNoteToDatabase(noteEntity: NoteEntity) {
        if (noteEntity.note.isNullOrEmpty()){
            MyLogger.d("DbTools - saveNoteToDatabase EMPTY note not saved titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
            return
        }
        MyLogger.d("DbTools - saveNoteToDatabase titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - saveNoteToDatabase db=null")
            return
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        noteRepo.insertNote(noteEntity)
    }

    suspend fun updateNoteInDatabase(noteEntity: NoteEntity) {
        if (noteEntity.note.isNullOrEmpty()){
            MyLogger.e("DbTools - updateNoteInDatabase EMPTY note not saved titleId=" + noteEntity.titleId  + " note=" + noteEntity.note + " date=" + noteEntity.date )
            return
        }
        MyLogger.d("DbTools - updateNoteInDatabase titleId=" + noteEntity.titleId + " note=" + noteEntity.note + " date=" + noteEntity.date )
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - updateNoteInDatabase db=null")
            return
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        noteRepo.updateNote(noteEntity)
    }

    suspend fun loadNoteFromDatabase(titleId:Int, date:String):String {
        MyLogger.d("DbTools - loadNoteFromDatabase titleId=" + titleId + " date=" + date)
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - loadNoteFromDatabase db=null")
            return ""
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        val noteEntity = noteRepo.getNote(titleId, date)
        if (noteEntity == null){
            MyLogger.e("DbTools - loadNoteFromDatabase not found")
            return ""
        }
        MyLogger.d("DbTools - loadNoteFromDatabase found note=" + noteEntity.note)
        return noteEntity.note!!
    }
    
    suspend fun loadNotesByTitleId(titleId: Int):MutableList<NoteEntity>? {
        MyLogger.d("DbTools - loadNotesByTitleId titleId=" + titleId)
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - loadNotesByTitleId db=null")
            return null
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        val notes = noteRepo.getNotesByTitleId(titleId)
        if (notes == null){
            MyLogger.e("DbTools - loadNotesByTitleId not found")
            return null
        }
        MyLogger.d("DbTools - loadNotesByTitleId found notes.size=" + notes.size)
        for (noteEntity in notes){
            MyLogger.d("-- id=" + noteEntity.id + " titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
        }

        return notes
    }

}