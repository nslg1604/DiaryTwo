package com.niaz.diary.db

import com.niaz.diary.MyApp
import com.niaz.diary.utils.MyLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DbTools {
    suspend fun saveNoteToDatabase(noteEntity: NoteEntity) {
        if (noteEntity.note.isNullOrEmpty()){
            MyLogger.e("DbTools - saveNoteToDatabase EMPTY note not saved titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
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
            MyLogger.e("DbTools - updateNoteInDatabase EMPTY note not saved titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
            return
        }
        MyLogger.e("DbTools - updateNoteInDatabase titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
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

    suspend fun loadNotesAll():MutableList<NoteEntity>? {
        MyLogger.d("DbTools - loadNotesAll")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - loadNotes db=null")
            return null
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        val notes = noteRepo.getNotes()
        if (notes == null){
            MyLogger.e("DbTools - loadNotesAll not found")
            return null
        }
        MyLogger.d("DbTools - loadNotesAll found notes.size=" + notes.size)
        for (noteEntity in notes){
            MyLogger.d("=== id=" + noteEntity.id + " titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
        }

        return notes
    }

//    fun loadNotesAsync(viewModelScope:CoroutineScope) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val notes:MutableList<NoteEntity>? = loadNotesByTitleId()
////            if (notes.isNullOrEmpty()){
////                MyLogger.d("loadNotesAsync EMPTY")
////                return@launch
////            }
////            for (noteEntity in notes){
////                MyLogger.d("loadNotesAsync id=" + noteEntity.id + " titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
////            }
//        }
//    }


}