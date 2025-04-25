package com.niaz.diary.mvi.list

import com.niaz.diary.data.title.TitleEntity

/**
 * Represents the state of the List screen
 */
data class ListState(
    // Data
    val titles: List<TitleEntity> = emptyList(),
    val message: String? = null,
    val selectedTitle: TitleEntity? = null,

    // UI state
    val showMenu: Boolean = false,
    val showAboutDialog: Boolean = false,
    val showAddTitleDialog: Boolean = false,
    val showMessageDialog: Boolean = false,
    val showTitleMenuDialog: Boolean = false,
    val showEditTitleDialog: Boolean = false
)

