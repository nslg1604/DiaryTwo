package com.niaz.diary.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niaz.diary.MyApp
import com.niaz.diary.R
import com.niaz.diary.db.TitleEntity
import com.niaz.diary.db.TitlesRepo
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _titleEntities = MutableStateFlow<List<TitleEntity>>(emptyList())
    val titleEntities: StateFlow<List<TitleEntity>> = _titleEntities

    private var _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun readTitleEntitiesFromDatabaseAsync() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val entities = readTitleEntitiesFromDatabase()
                if (entities.isNullOrEmpty()){
                    initTitleEntities()
                }
                else {
                    _titleEntities.value = entities!!.toList()
                }
            }
        }
    }

    suspend fun readTitleEntitiesFromDatabase():List<TitleEntity>? {
        MyLogger.d("ListViewModel - readTitlesFromDatabase")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("ListViewModel - readTitlesFromDatabase db=null")
            return null
        }
        val titleDao = db.titleDao()
        val titlesRepo = TitlesRepo(titleDao = titleDao)
        val titleEntities = titlesRepo.getTitles()
        MyLogger.d("ListViewModel - readTitlesFromDatabase title.size=" + titleEntities?.size)

        titleEntities.forEach{ titleEntity ->
            MyLogger.d("ListViewModel - titleEntity id=${titleEntity.id} title=${titleEntity.title} ")
        }
        MyData.titleEntities = titleEntities
        return titleEntities
    }

    suspend fun initTitleEntities(){
        addTitleToDatabase(TitleEntity(context.getString(R.string.title_1)))
        addTitleToDatabase(TitleEntity(context.getString(R.string.title_2)))
        addTitleToDatabase(TitleEntity(context.getString(R.string.title_3)))
        addTitleToDatabase(TitleEntity(context.getString(R.string.title_4)))
    }

    fun addTitleToDatabaseAsync(titleEntity:TitleEntity) {
        MyLogger.d("ListViewModel - addTitleToDatabaseAsync")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    addTitleToDatabase(titleEntity)
                } catch (e: Exception) {
                    MyLogger.e("ListViewModel - addTitleToDatabaseAsync error=" + e)
                }
            }
        }
    }

    suspend fun addTitleToDatabase(titleEntity: TitleEntity) {
        MyLogger.d("ListViewModel - addTitleToDatabase")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("ListViewModel - addTitleToDatabase db=null")
            return
        }
        MyLogger.d("ListViewModel - addTitlesToDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
        val titleDao = db.titleDao()
        val titlesRepo = TitlesRepo(titleDao = titleDao)
        val idNew = titlesRepo.insertTitleEntity(titleEntity) // add one title
        val titleEntityNew = titleEntity.copy(id = idNew!!.toInt())
        _titleEntities.update { currentList -> currentList + titleEntityNew }
        MyData.titleEntities.add(titleEntityNew)
    }

    fun updateTitleInDatabaseAsync(titleEntity:TitleEntity) {
        MyLogger.d("ListViewModel - updateTitleInDatabaseAsync")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    updateTitleInDatabase(titleEntity)
                    _titleEntities.update { oldList ->
                        oldList.map { if (it.id == titleEntity.id) titleEntity else it }
                    }
                } catch (e: Exception) {
                    MyLogger.e("ListViewModel - updateTitleInDatabaseAsync error=" + e)
                }
            }
        }
    }

    suspend fun updateTitleInDatabase(titleEntity: TitleEntity) {
        MyLogger.d("ListViewModel - updateTitleInDatabase")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("ListViewModel - updateTitleInDatabase db=null")
            return
        }
        MyLogger.d("ListViewModel - updateTitleInDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
        val titleDao = db.titleDao()
        val titlesRepo = TitlesRepo(titleDao = titleDao)
        titlesRepo.updateTitleEntity(titleEntity)
    }

    suspend fun deleteTitleInDatabase(titleEntity: TitleEntity) {
        MyLogger.d("ListViewModel - deleteTitleInDatabase")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("ListViewModel - deleteTitleInDatabase db=null")
            return
        }
        MyLogger.d("ListViewModel - deleteTitleInDatabase title.id=" + titleEntity.id + " title=" + titleEntity.title)
        val titleDao = db.titleDao()
        val titlesRepo = TitlesRepo(titleDao = titleDao)
        titlesRepo.deleteTitleEntity(titleEntity)
    }


    fun deleteTitleInDatabaseAsync(titleEntity:TitleEntity) {
        MyLogger.d("ListViewModel - deleteTitleInDatabaseAsync")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    deleteTitleInDatabase(titleEntity)
                    _titleEntities.update { oldList ->
                        oldList.filterNot { it.id == titleEntity.id }
                    }
                } catch (e: Exception) {
                    MyLogger.e("ListViewModel - deleteTitleInDatabaseAsync error=" + e)
                }
            }
        }
    }

    fun resetMessage() {
        _message.value = ""
    }

    fun onExportDatabase() {
        MyLogger.d("ListViewModel - onExportDatabase")
        val databasePath = context.getDatabasePath(MyConst.DB_NAME)

        if (!databasePath.exists()) {
            MyLogger.e("ListViewModel - onExportDatabase - not found")
            _message.value = context.resources.getString(R.string.db_not_found, MyConst.DB_NAME)
            return
        }
        val destDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        val destPath = "/" + Environment.DIRECTORY_DOWNLOADS + "/" + MyConst.DB_NAME
        val destFile = File(destDir, MyConst.DB_NAME)
        if (destFile.exists()){
            destFile.delete()
        }
        try {
            databasePath.copyTo(destFile, overwrite = true)
            MyLogger.d("ListViewModel - onExportDatabase - exported")
            _message.value = context.resources.getString(R.string.db_exported, destPath)
        } catch (e: Exception) {
            MyLogger.e("ListViewModel - onExportDatabase - error=" + e)
            _message.value = context.resources.getString(R.string.db_export_error, destPath)
        }
    }

    fun importDatabaseFromUri(context:Context, uri: Uri) {
        val sourcePath = getFileNameFromUri(context, uri)
        MyLogger.d("ListViewModel - importDatabaseFromUri $sourcePath")
        try {
            MyApp.getInstance().closeDatabase()

            val inputStream = context.contentResolver.openInputStream(uri)
            val databaseFile = context.getDatabasePath(MyConst.DB_NAME)

            inputStream?.use { input ->
                databaseFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            MyLogger.d("ListViewModel - onImportDatabase - imported")
            _message.value = context.resources.getString(R.string.db_imported, sourcePath)

            readTitleEntitiesFromDatabaseAsync()

        } catch (e: Exception) {
            MyLogger.e("ListViewModel - onImportDatabase - error=" + e)
            _message.value = context.resources.getString(R.string.db_import_error, sourcePath)
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

}