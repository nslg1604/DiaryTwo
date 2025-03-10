package com.niaz.diary.db

class TitlesRepo(private val titleDao: TitleDao) {
    suspend fun insertTitleEntity(title: TitleEntity) {
        titleDao.insertTitle(title)
    }

    suspend fun getTitles(): MutableList<TitleEntity> {
        return titleDao.getTitles()
    }

    suspend fun deleteTitleById(id: Int) {
        titleDao.deleteTitleById(id)
    }

}