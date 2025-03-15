package com.niaz.diary.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niaz.diary.MyApp
import com.niaz.diary.R
import com.niaz.diary.db.TitleEntity
import com.niaz.diary.db.TitlesRepo
import com.niaz.diary.utils.MyData
import com.niaz.diary.utils.MyLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _titleEntities = MutableStateFlow<List<TitleEntity>>(emptyList())
    val titleEntities: StateFlow<List<TitleEntity>> = _titleEntities

    fun readTitleEntitiesFromDatabaseAsync() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var entities = readTitleEntitiesFromDatabase()
                if (entities.isNullOrEmpty()){
                    entities = initTitleEntities()
                    addTitlesToDatabase(entities)
                }
                _titleEntities.value = entities
            }
        }
    }

    suspend fun addTitlesToDatabase(titleEntities:MutableList<TitleEntity>) {
        MyLogger.d("ListViewModel - addTitlesToDatabase")
        val db = MyApp.getInstance().getDatabase()
        if (db == null) {
            MyLogger.e("ListViewModel - addTitlesToDatabase db=null")
            return
        }
        val titleDao = db.titleDao()
        val titlesRepo = TitlesRepo(titleDao = titleDao)
        for (titleEntity in titleEntities) {
            MyLogger.d("ListViewModel - addTitlesToDatabase title=" + titleEntity.title + " size before=" + this.titleEntities.value.size)
            titlesRepo.insertTitleEntity(titleEntity)
            MyData.titleEntities.add(titleEntity)
        }
    }


    suspend fun readTitleEntitiesFromDatabase():MutableList<TitleEntity>? {
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
            MyLogger.d("titleEntity id=${titleEntity.id} title=${titleEntity.title} ")
        }
        MyData.titleEntities = titleEntities
        return titleEntities
    }

    fun addTitlesToDatabaseAsync(titlesEntities:MutableList<TitleEntity>) {
        var titlesEntities:MutableList<TitleEntity> = ArrayList()
        MyLogger.d("ListViewModel - addTitlesToDatabaseAsync")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    addTitlesToDatabase(titlesEntities)
                } catch (e: Exception) {
                    MyLogger.e("ListViewModel - addTitlesToDatabaseAsync error=" + e)
                }
            }
        }
    }

    fun initTitleEntities(): MutableList<TitleEntity> {
        var titlesEntities:MutableList<TitleEntity> = ArrayList()
        titlesEntities.add(TitleEntity(context.getString(R.string.title_1)))
        titlesEntities.add(TitleEntity(context.getString(R.string.title_2)))
        titlesEntities.add(TitleEntity(context.getString(R.string.title_3)))
        titlesEntities.add(TitleEntity(context.getString(R.string.title_4)))
        return titlesEntities
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
        titlesRepo.insertTitleEntity(titleEntity)
        MyData.titleEntities.add(titleEntity)  // add one titleEntity

    }


}