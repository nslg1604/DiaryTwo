package com.niaz.diary.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TitleDao {
    @Insert(entity = TitleEntity::class)
    fun insertTitle(title: TitleEntity):Long?

    @Query("SELECT id, title FROM titles\n")
    fun getTitles(): MutableList<TitleEntity>

    @Query("DELETE FROM titles WHERE id = :id")
    fun deleteTitleById(id: Int)

    @Query("UPDATE titles SET title = :newTitle WHERE id = :id")
    suspend fun updateTitle(id: Int, newTitle: String): Int
}