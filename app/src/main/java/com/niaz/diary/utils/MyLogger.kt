package com.niaz.diary.utils

import android.util.Log

class MyLogger {
    companion object {
        val MAX_LOG = 2500
        val TAG = "newapp"

        fun d(str: String) {
//            if (str.length <= max) {
//                Log.d(TAG, str)
//                return
//            }
//            if (!MyConst.LOG_ALL){
//                Log.d(TAG, str.substring(0,  max))
//                return
//            }
            if (!MyConst.LOG_ALL){
                Log.d(TAG, str.substring(0, Math.min(str.length, MAX_LOG)))
                return
            }

            var txt = str
            while (txt.length > MAX_LOG){
                Log.d(TAG, txt.substring(0,MAX_LOG))
                txt = txt.substring(MAX_LOG)
            }
            Log.d(TAG, txt)
        }

        fun v(str: String) {
            d(str)
        }

        fun e(str: String) {
            Log.e(TAG, str)
        }

        fun s(str: String) {
            Log.e(TAG, str)
        }
    }
}