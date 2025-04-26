package com.niaz.diary

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.niaz.diary.data.AppDatabase
import com.niaz.diary.utils.MyConst
import com.niaz.diary.utils.MyDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
    private var database: AppDatabase? = null

//    @Inject lateinit var timberTree: Timber.Tree

    override fun onCreate() {
        super.onCreate()
        myApp = this
        Timber.plant(MyDebugTree())
//        Timber.plant(timberTree)
        Timber.d("MyApp - onCreate")
    }


    companion object {
        lateinit var myApp: MyApp
        fun getInstance(): MyApp {
            return myApp
        }
    }

    fun createDatabase() {
        Timber.d("MyApp - createDatabase")
        database = databaseBuilder(
            this,
            AppDatabase::class.java,
            MyConst.DB_NAME
        ).build()
        if (database == null){
            Timber.e("MyApp - error creating database")
        }
    }

    fun getDatabase(): AppDatabase? {
        if (database == null){
            createDatabase()
        }
        return database
    }

    fun closeDatabase() {
        database?.close()
        database = null
    }





}
