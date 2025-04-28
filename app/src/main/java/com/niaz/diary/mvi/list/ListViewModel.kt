package com.niaz.diary.mvi.list

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niaz.diary.MyApp
import com.niaz.diary.R
import com.niaz.diary.data.title.TitleEntity
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val titleRepo: TitleRepo
) : ViewModel() {

    // MVI State
    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    // Process user intent
    fun processIntent(intent: ListIntent) {
        when (intent) {
            // Data loading
            is ListIntent.LoadTitles -> {
                loadTitlesAsync()
            }

            // Title management
            is ListIntent.AddTitle -> {
                addTitleAsync(intent.title)
            }

            is ListIntent.UpdateTitle -> {
                updateTitleInDatabase(intent.title)
            }

            is ListIntent.DeleteTitle -> {
                deleteTitleFromDatabase(intent.title)
            }

            // Database import/export
            is ListIntent.ImportDatabase -> {
                importDatabaseFromUri(intent.context, intent.uri)
            }

            is ListIntent.ExportDatabase -> {
                exportDatabaseFromUri(intent.context, intent.uri)
            }

            // UI state management
            is ListIntent.ShowAddTitleDialog -> {
                _state.update { it.copy(showAddTitleDialog = true) }
            }

            is ListIntent.HideAddTitleDialog -> {
                _state.update { it.copy(showAddTitleDialog = false) }
            }

            is ListIntent.ShowTitleMenuDialog -> {
                _state.update {
                    it.copy(
                        showTitleMenuDialog = true,
                        selectedTitle = intent.title
                    )
                }
            }

            is ListIntent.HideTitleMenuDialog -> {
                _state.update { it.copy(showTitleMenuDialog = false) }
            }

            is ListIntent.ShowEditTitleDialog -> {
                _state.update { it.copy(showEditTitleDialog = true) }
            }

            is ListIntent.HideEditTitleDialog -> {
                _state.update { it.copy(showEditTitleDialog = false) }
            }

            is ListIntent.ShowMessageDialog -> {
                _state.update { it.copy(showMessageDialog = true) }
            }

            is ListIntent.HideMessageDialog -> {
                _state.update { it.copy(showMessageDialog = false, message = null) }
            }

            is ListIntent.ShowAboutDialog -> {
                _state.update { it.copy(showAboutDialog = true) }
            }

            is ListIntent.HideAboutDialog -> {
                _state.update { it.copy(showAboutDialog = false) }
            }

            is ListIntent.ToggleMenu -> {
                _state.update { it.copy(showMenu = !it.showMenu) }
            }

            is ListIntent.ShowMenu -> {
                _state.update { it.copy(showMenu = true) }
            }

            is ListIntent.HideMenu -> {
                _state.update { it.copy(showMenu = false) }
            }
        }
    }

    private fun loadTitlesAsync() {
        Timber.d("ListViewModel - loadTitlesAsync")
        viewModelScope.launch {
            try {
                loadTitles()
            } catch (e: Exception) {
                Timber.d("Error loading titles: ${e.message}")
                showMessage("Error loading titles: ${e.message}")
            }
        }
    }

    suspend fun loadTitles() {
        Timber.d("ListViewModel - loadTitles")
        var titles = titleRepo.getTitles()
        if (titles.isEmpty()) {
            initTitleEntities()
            titles = titleRepo.getTitles()
        }
        MyData.titleEntities = titles
        _state.update { it.copy(titles = titles) }
    }

    private fun addTitleAsync(title: TitleEntity) {
        Timber.d("ListViewModel - addTitleAsync title=" + title.title)
        viewModelScope.launch {
            try {
                titleRepo.insertTitle(title)
                loadTitlesAsync() // Refresh the list
            } catch (e: Exception) {
                Timber.d("Error adding title: ${e.message}")
                showMessage("Error adding title: ${e.message}")
            }
        }
    }

    private fun updateTitleInDatabase(title: TitleEntity) {
        viewModelScope.launch {
            try {
                titleRepo.updateTitle(title)
                loadTitlesAsync() // Refresh the list
            } catch (e: Exception) {
                Timber.d("Error updating title: ${e.message}")
                showMessage("Error updating title: ${e.message}")
            }
        }
    }

    private fun deleteTitleFromDatabase(title: TitleEntity) {
        viewModelScope.launch {
            try {
                titleRepo.deleteTitle(title)
                loadTitlesAsync() // Refresh the list
            } catch (e: Exception) {
                Timber.d("Error deleting title: ${e.message}")
                showMessage("Error deleting title: ${e.message}")
            }
        }
    }

    fun exportDatabaseFromUri(context: Context, uri: Uri) {
        val destPath = getFileNameFromUri(context, uri)
        try {
            val dbPath = File(context.filesDir.parent, "databases/${MyConst.DB_NAME}")
            val outputStream = context.contentResolver.openOutputStream(uri)

            FileInputStream(dbPath).use { input ->
                outputStream?.use { output ->
                    input.copyTo(output)
                }
            }
            Timber.d("ExportDatabaseTool - db exported")
            showMessage(context.resources.getString(R.string.db_exported, destPath))
        } catch (e: Exception) {
            Timber.e("ExportDatabaseTool - export error=$e")
            showMessage(context.resources.getString(R.string.db_export_error, destPath))
        }
    }

    fun importDatabaseFromUri(context: Context, uri: Uri) {
        val sourcePath = getFileNameFromUri(context, uri)
        Timber.d("ListViewModel - importDatabaseFromUri $sourcePath")
        try {
            MyApp.getInstance().closeDatabase()

            val inputStream = context.contentResolver.openInputStream(uri)
            val databaseFile = context.getDatabasePath(MyConst.DB_NAME)

            inputStream?.use { input ->
                databaseFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            Timber.d("ListViewModel - onImportDatabase - imported")
            showMessage(context.resources.getString(R.string.db_imported, sourcePath))

            loadTitlesAsync()

        } catch (e: Exception) {
            Timber.e("ListViewModel - onImportDatabase - error=" + e)
            showMessage(context.resources.getString(R.string.db_import_error, sourcePath))
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        returnCursor?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }


    private fun showMessage(message: String) {
        _state.update {
            it.copy(
                message = message,
                showMessageDialog = true
            )
        }
    }


    suspend fun initTitleEntities() {
        val context = MyApp.getInstance()
        titleRepo.apply {
            insertTitle(TitleEntity(context.getString(R.string.title_1)))
            insertTitle(TitleEntity(context.getString(R.string.title_2)))
            insertTitle(TitleEntity(context.getString(R.string.title_3)))
            insertTitle(TitleEntity(context.getString(R.string.title_4)))
        }
    }
}

/**
 * Interface for the Title repository
 * This would be implemented by a concrete class that interacts with your database
 */
interface TitleRepo {
    suspend fun getTitles(): List<TitleEntity>
    suspend fun insertTitle(title: TitleEntity)
    suspend fun updateTitle(title: TitleEntity)
    suspend fun deleteTitle(title: TitleEntity)
}
