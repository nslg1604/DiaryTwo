package com.niaz.diary.data.title

import com.niaz.diary.mvi.list.TitleRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of the TitleRepository interface
 * This class interacts with the actual database through the DAO
 */
@Singleton
class TitleRepoImpl @Inject constructor(
    private val titleDao: TitleDao
) : TitleRepo {

    override suspend fun getTitles(): List<TitleEntity> = withContext(Dispatchers.IO) {
        return@withContext titleDao.getTitles()
    }

    override suspend fun insertTitle(title: TitleEntity) = withContext(Dispatchers.IO) {
        titleDao.insertTitle(title)
    }

    override suspend fun updateTitle(title: TitleEntity) = withContext(Dispatchers.IO) {
        titleDao.updateTitle(title.id, title.title)
    }

    override suspend fun deleteTitle(title: TitleEntity) = withContext(Dispatchers.IO) {
        titleDao.deleteTitleById(title.id)
    }
}