package com.niaz.diary.data.title

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import androidx.room.*

@Dao
interface TitleDao {
    @Query("SELECT * FROM titles")
    fun getTitles(): MutableList<TitleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitle(title: TitleEntity)

//    @Update
//    fun updateTitle(title: TitleEntity)
//
//    @Delete
//    fun deleteTitle(title: TitleEntity)
//}
//
////@Dao
////interface TitleDao {
////    @Insert(entity = TitleEntity::class)
////    fun insertTitle(title: TitleEntity)
////
////    @Query("SELECT id, title FROM titles\n")
////    fun getTitles(): MutableList<TitleEntity>
////
    @Query("DELETE FROM titles WHERE id = :id")
    fun deleteTitleById(id: Int)

    @Query("UPDATE titles SET title = :newTitle WHERE id = :id")
    suspend fun updateTitle(id: Int, newTitle: String)
}