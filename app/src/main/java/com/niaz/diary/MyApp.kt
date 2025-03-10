package com.niaz.diary

import android.app.Application
import androidx.room.Room.databaseBuilder
import com.niaz.diary.db.AppDatabase
import com.niaz.diary.utils.MyLogger
//import dagger.hilt.android.HiltAndroidApp

//@HiltAndroidApp
class MyApp : Application() {
    private var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        myApp = this.applicationContext as MyApp
        MyLogger.d("MyApp - onCreate")
//        setUncaughtException()
    }

    companion object {
        lateinit var myApp: MyApp
        fun getInstance(): MyApp {
            return myApp
        }
    }

    fun createDatabase() {
        MyLogger.d("MyApp - createDatabase")
        database = databaseBuilder(
            this, AppDatabase::
            class.java, "database"
        ).build()
        if (database == null){
            MyLogger.e("MyApp - error creating database")
        }
    }

    fun getDatabase(): AppDatabase? {
        if (database == null){
            createDatabase()
        }
        return database
    }




}
