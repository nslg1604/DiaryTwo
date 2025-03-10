package com.niaz.diary.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TitleDao {
    @Insert(entity = TitleEntity::class)
    fun insertTitle(title: TitleEntity)

    @Query("SELECT id, title FROM titles\n")
    fun getTitles(): MutableList<TitleEntity>

    @Query("DELETE FROM titles WHERE id = :id")
    fun deleteTitleById(id: Int)


}