package com.niaz.diary.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import java.util.Calendar

class ShowViewModel : ViewModel() {
    private val dbTools = DbTools()
    private val myCalendar = MyCalendar()
    private var calendar = Calendar.getInstance()
    private val _text = MutableLiveData<String>().apply {
        value = "This is EditViewModel"
    }
    val text: LiveData<String> = _text

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()

    fun readMyNotes(titleId:Int): Flow<MutableList<NoteEntity>?> = flow {
        dbTools.loadNotesAll() // for debug
        val myNotes = dbTools.loadNotesByTitleId(titleId)
        emit(myNotes)
    }.flowOn(Dispatchers.IO)

    fun getDate(calendar: Calendar): String {
        val date = myCalendar.calendarToDD_MM_YYYY(calendar) + ", " +
                myCalendar.getDayOfWeekShort(calendar)
        this.calendar = calendar.clone() as Calendar
        return date
    }

    fun getInfo(infoType: String, offset: Int): String {
        MyLogger.d("EditViewModel - getInfo infoType=$infoType offset=$offset")
        if (infoType.equals(MyConst.INFO_CALENDAR)) {
            var calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, offset)
            val date = getDate(calendar)
            MyLogger.d("EditViewModel - getInfo date=$date")
            return date
        }
        if (infoType.equals(MyConst.INFO_TITLES)) {
            MyData.iTitle = offset
            val title = MyData.titleEntities.get(MyData.iTitle).title
            MyLogger.d("EditViewModel - getInfo data=$title")
            return title!!
        }
        return ""
    }


}