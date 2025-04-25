package com.niaz.diary.mvi.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niaz.diary.data.DbTools
import com.niaz.diary.mvi.edit.EditState
import com.niaz.diary.mvi.edit.EditEvent
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import com.niaz.diary.data.note.NoteEntity
import com.niaz.diary.utils.MyCalendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
) : ViewModel() {
    private var calendar = Calendar.getInstance()
    @Inject
    lateinit var myCalendar: MyCalendar
    @Inject
    lateinit var dbTools: DbTools

    private val _state = MutableStateFlow(EditState())
    val state: StateFlow<EditState> = _state.asStateFlow()

    fun onEvent(event: EditEvent) {
        when (event) {
            is EditEvent.TitleOffsetChanged -> {
                MyLogger.d("EditViewModel - EditEvent.TitleOffsetChanged")
                val newOffset = event.offset.coerceIn(0, MyData.titleEntities.lastIndex)
                MyData.iTitle = newOffset
                _state.update { it.copy(offsetTitle = newOffset) }
            }

            is EditEvent.CalendarOffsetChanged -> {
                MyLogger.d("EditViewModel - CalendarOffsetChanged offs=${event.offset}")
                _state.update { it.copy(offsetCalendar = event.offset) }
                saveNote()
            }

            is EditEvent.NoteChanged -> {
                MyLogger.d("EditViewModel - NoteChanged note=${event.note}")
                _state.update { it.copy(note = event.note) }
                saveNote()
            }

            EditEvent.BackClicked -> {
                saveNote()
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = NoteEntity(
                titleId = MyData.titleEntities[_state.value.offsetTitle].id,
                date = MyData.date,
                note = _state.value.note
            )
            MyLogger.d("saveNote titleId=${entity.titleId} date=${entity.date} note=${entity.note}")
            val loadedNote = dbTools.loadNoteFromDatabase(
                entity.titleId, entity.date!!)

            if (loadedNote.isEmpty()) {
                dbTools.saveNoteToDatabase(entity)
            } else {
                dbTools.updateNoteInDatabase(entity)
            }
            MyLogger.d("saveNote - end")
        }
    }

    fun readNote(): Flow<String> = flow {
        MyLogger.d("EditViewModel - readNote iTitle=${MyData.iTitle} id=${MyData.titleEntities[MyData.iTitle].id} date=${MyData.date}")
        val loadedNote = dbTools.loadNoteFromDatabase(
            MyData.titleEntities[MyData.iTitle].id,
            MyData.date
        )
        emit(loadedNote ?: "")
    }.flowOn(Dispatchers.IO)

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

    fun getDate(calendar: Calendar): String {
        val date = myCalendar.calendarToDD_MM_YYYY(calendar) + ", " +
                myCalendar.getDayOfWeekShort(calendar)
        this.calendar = calendar.clone() as Calendar
        return date
    }


}
