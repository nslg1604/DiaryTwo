package com.niaz.diary.mvi.list

import android.content.Context
import android.net.Uri
import com.niaz.diary.data.title.TitleEntity


/**
 * Represents the user actions that can happen on the List screen
 */
sealed class ListIntent {
    // Data loading intents
    object LoadTitles : ListIntent()

    // Title management intents
    data class AddTitle(val title: TitleEntity) : ListIntent()
    data class UpdateTitle(val title: TitleEntity) : ListIntent()
    data class DeleteTitle(val title: TitleEntity) : ListIntent()

    // Database import/export intents
    data class ImportDatabase(val uri: Uri, val context: Context) : ListIntent()
    data class ExportDatabase(val uri: Uri, val context: Context) : ListIntent()

    // UI state intents
    object ShowAddTitleDialog : ListIntent()
    object HideAddTitleDialog : ListIntent()

    data class ShowTitleMenuDialog(val title: TitleEntity) : ListIntent()
    object HideTitleMenuDialog : ListIntent()

    object ShowEditTitleDialog : ListIntent()
    object HideEditTitleDialog : ListIntent()

    object ShowMessageDialog : ListIntent()
    object HideMessageDialog : ListIntent()

    object ShowAboutDialog : ListIntent()
    object HideAboutDialog : ListIntent()

    object ToggleMenu : ListIntent()
    object ShowMenu : ListIntent()
    object HideMenu : ListIntent()
}