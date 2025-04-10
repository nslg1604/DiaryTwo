package com.niaz.diary.db

class TitlesRepo(private val titleDao: TitleDao) {
    suspend fun insertTitleEntity(title: TitleEntity):Long? {
        return titleDao.insertTitle(title)!!
    }

    suspend fun updateTitleEntity(titleEntity: TitleEntity) {
        titleDao.updateTitle(titleEntity.id, titleEntity.title)
    }

    suspend fun getTitles(): MutableList<TitleEntity> {
        return titleDao.getTitles()
    }

    suspend fun deleteTitleEntity(titleEntity: TitleEntity) {
        titleDao.deleteTitleById(titleEntity.id)
    }

}