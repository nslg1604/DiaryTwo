package com.niaz.diary.mvi.edit

sealed class EditEvent {
    object BackClicked : EditEvent()
    data class TitleOffsetChanged(val offset: Int) : EditEvent()
    data class CalendarOffsetChanged(val offset: Int) : EditEvent()
    data class NoteChanged(val note: String) : EditEvent()
}
