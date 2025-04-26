package com.niaz.diary.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.niaz.diary.data.DbTools
import com.niaz.diary.data.note.NoteEntity
import com.niaz.diary.utils.MyCalendar
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import timber.log.Timber
import com.niaz.diary.utils.GraphBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShowViewModel @Inject constructor(): ViewModel() {
    private val dbTools = DbTools()
    private var calendar = Calendar.getInstance()
    @Inject lateinit var myCalendar: MyCalendar
    @Inject lateinit var graphBuilder: GraphBuilder

    private val _graphBitmap = MutableStateFlow<Bitmap?>(null)
    val graphBitmap: StateFlow<Bitmap?> get() = _graphBitmap

    var myNotes: MutableList<NoteEntity>? = null
        private set

    fun readMyNotes(titleId: Int): Flow<MutableList<NoteEntity>?> = flow {
//        dbTools.loadNotesAll() // debug
        myNotes = dbTools.loadNotesByTitleId(titleId)
        _graphBitmap.value = graphBuilder.buildGraph(myNotes ?: emptyList())
        emit(myNotes)
    }.flowOn(Dispatchers.IO)

    fun getDate(calendar: Calendar): String {
        val date = myCalendar.calendarToDD_MM_YYYY(calendar) + ", " +
                myCalendar.getDayOfWeekShort(calendar)
        this.calendar = calendar.clone() as Calendar
        return date
    }

    fun getInfo(infoType: String, offset: Int): String {
        Timber.d("EditViewModel - getInfo infoType=$infoType offset=$offset")
        return when (infoType) {
            MyConst.INFO_CALENDAR -> {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, offset)
                getDate(calendar)
            }
            MyConst.INFO_TITLES -> {
                MyData.iTitle = offset
                MyData.titleEntities[offset].title
            }
            else -> ""
        }
    }
}
