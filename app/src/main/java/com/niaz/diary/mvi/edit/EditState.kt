package com.niaz.diary.mvi.edit

import com.niaz.diary.utils.MyData

data class EditState(
    val offsetTitle: Int = MyData.iTitle,
    val offsetCalendar: Int = 0,
    val note: String = ""
)
