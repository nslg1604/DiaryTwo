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
        MyLogger.e("DbTools - saveNoteToDatabase titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - saveNoteToDatabase db=null")
            return
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        noteRepo.insertNote(noteEntity)

        loadNotes()
    }

    suspend fun loadNoteFromDatabase(titleId:Int, date:String):String {
        MyLogger.e("DbTools - loadNoteFromDatabase titleId=" + titleId + " date=" + date)
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - loadNoteFromDatabase db=null")
            return ""
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        val noteEntity = noteRepo.getNote(titleId, date)
        if (noteEntity == null){
            MyLogger.d("DbTools - loadNoteFromDatabase not found")
            return ""
        }
        MyLogger.d("DbTools - loadNoteFromDatabase found note=" + noteEntity.note)
        return noteEntity.note!!
    }
    
    suspend fun loadNotes():MutableList<NoteEntity>? {
        MyLogger.d("DbTools - loadNotes")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("DbTools - loadNotes db=null")
            return null
        }
        val noteDao = db.noteDao()
        val noteRepo = NoteRepo(noteDao = noteDao)
        val notes = noteRepo.getNotes()
        if (notes == null){
            MyLogger.d("DbTools - loadNotes not found")
            return null
        }
        MyLogger.d("DbTools - loadNotes found notes.size=" + notes.size)
        for (noteEntity in notes){
            MyLogger.d("=== id=" + noteEntity.id + " titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
        }

        return notes
    }

    fun loadNotesAsync(viewModelScope:CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            val notes:MutableList<NoteEntity>? = loadNotes()
//            if (notes.isNullOrEmpty()){
//                MyLogger.d("loadNotesAsync EMPTY")
//                return@launch
//            }
//            for (noteEntity in notes){
//                MyLogger.d("loadNotesAsync id=" + noteEntity.id + " titleId=" + noteEntity.titleId + " date=" + noteEntity.date + " note=" + noteEntity.note)
//            }
        }
    }


}