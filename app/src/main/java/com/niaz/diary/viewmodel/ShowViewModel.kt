package com.niaz.diary.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.niaz.diary.data.MyNote
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyCalendar
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Calendar

class ShowViewModel : ViewModel() {
    private val myCalendar = MyCalendar()
    private var calendar = Calendar.getInstance()
    private val _text = MutableLiveData<String>().apply {
        value = "This is EditViewModel"
    }
    val text: LiveData<String> = _text

    private val _note = MutableStateFlow("")
    val note: StateFlow<String> = _note.asStateFlow()
    var iNote = 0
    var myNotes:MutableList<MyNote> = ArrayList()

    fun initMyNotes(){
        myNotes.add(MyNote(1, "11-09-2003", "3"))
        myNotes.add(MyNote(2, "12-02-2023", "53"))
        myNotes.add(MyNote(3, "19-09-2024", "63"))
        myNotes.add(MyNote(4, "21-10-2024", "17"))
    }

    fun readMyNotes(offsetTitle:Int): Flow<List<MyNote>> = flow {
        val myNotes = loadMyNotesFromDatabase(offsetTitle)
        emit(myNotes)
    }.flowOn(Dispatchers.IO)


    fun loadMyNotesFromDatabase(offsetTitle:Int):List<MyNote>{
        val myNotes:MutableList<MyNote> = ArrayList()
        for (i in 0 until 3){
            myNotes.add(MyNote(offsetTitle,
                "dateA" + i.toString(),
                (System.currentTimeMillis() / 1000).toString()))
        }
        MyLogger.d("ShowViewModel - loadMyNotesFromDatabase size=" + myNotes.size)
        return myNotes
    }

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