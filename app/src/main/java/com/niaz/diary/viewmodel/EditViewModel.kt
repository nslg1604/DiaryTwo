package com.niaz.diary.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niaz.diary.db.DbTools
import com.niaz.diary.db.NoteEntity
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyCalendar
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Calendar

class EditViewModel : ViewModel() {
    private val dbTools = DbTools()
    private val myCalendar = MyCalendar()
    private var calendar = Calendar.getInstance()
    private val _text = MutableLiveData<String>().apply {
        value = "This is EditViewModel"
    }
    val text: LiveData<String> = _text

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    fun readNote(): Flow<String> = flow {
        MyLogger.d("EditViewModel - readNote iTitle=" + MyData.iTitle + "/" + MyData.titleEntities.get(MyData.iTitle).id + " date=" + MyData.date)
        val loadedNote = dbTools.loadNoteFromDatabase(
            MyData.titleEntities.get(MyData.iTitle).id,
            MyData.date
        )
        emit(loadedNote)
    }.flowOn(Dispatchers.IO)

    fun updateNoteAsync(newNote: String) {
        MyLogger.d("EditViewModel - updateNoteAsync " + MyData.iTitle + "/" + MyData.titleEntities.get(MyData.iTitle).id + " " + MyData.date + "/" + newNote)
        val noteEntity = NoteEntity(
            MyData.titleEntities.get(MyData.iTitle).id,
            MyData.date,
            newNote
        )

        viewModelScope.launch(Dispatchers.IO) {
            val loadedNote = dbTools.loadNoteFromDatabase(
                MyData.titleEntities.get(MyData.iTitle).id,
                MyData.date
            )
            MyLogger.d("EditViewModel - updateNoteAsync exists " + MyData.iTitle + "/" + MyData.titleEntities.get(MyData.iTitle).id + " " + MyData.date + "/" + loadedNote)

            if (loadedNote.isEmpty()) {
                dbTools.saveNoteToDatabase(noteEntity)
            }
            else {
                dbTools.updateNoteInDatabase(noteEntity)
            }
            _note.value = newNote // update StateFlow
        }
    }

    fun getDate(calendar: Calendar): String {
        val date = myCalendar.calendarToDD_MM_YYYY(calendar) + ", " +
                myCalendar.getDayOfWeekShort(calendar)
        this.calendar = calendar.clone() as Calendar
        return date
    }

    fun getInfo(infoType: String, offset: Int): String {
//        MyLogger.d("EditViewModel - getInfo infoType=$infoType offset=$offset")
        if (infoType.equals(MyConst.INFO_CALENDAR)) {
            var calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, offset)
            val date = getDate(calendar)
            MyData.date = date
//            MyLogger.d("EditViewModel - getInfo date=$date")
            return date
        }
        if (infoType.equals(MyConst.INFO_TITLES)) {
            MyData.iTitle = offset
            val title = MyData.titleEntities.get(offset).title
//            MyLogger.d("EditViewModel - getInfo iTitle=" + offset + " title=$title")
            return title
        }
        return ""
    }


}