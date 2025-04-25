package com.niaz.diary.data

import android.content.Context
import androidx.room.Room
import com.niaz.diary.data.title.TitleRepoImpl
import com.niaz.diary.data.title.TitleDao
import com.niaz.diary.mvi.list.TitleRepo
import com.niaz.diary.utils.MyConst
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            MyConst.DB_NAME
        )
            .fallbackToDestructiveMigration() // This will recreate the database if schema changes
            .build()
    }

    @Provides
    @Singleton
    fun provideTitleDao(appDatabase: AppDatabase): TitleDao {
        return appDatabase.titleDao()
    }

    @Provides
    @Singleton
    fun provideTitleRepository(titleDao: TitleDao): TitleRepo {
        return TitleRepoImpl(titleDao)
    }
}