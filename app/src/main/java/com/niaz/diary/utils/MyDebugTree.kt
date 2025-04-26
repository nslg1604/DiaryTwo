package com.niaz.diary.utils

import timber.log.Timber

/**
 * Simple log
 */
class MyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        val fullClassName = element.className
        val simpleClassName = fullClassName.substringAfterLast('.')
        return "$simpleClassName.${element.methodName}"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "newapp", message, t)
    }
}

/**
 * Log with class.method
 */
//class MyDebugTree : Timber.DebugTree() {
//    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
//        val stackTrace = Throwable().stackTrace
//        val element = stackTrace.getOrNull(5)
//
//        val newTag = "newapp"
//        val prefix = if (element != null) {
//            val className = element.className.substringAfterLast('.')
//            val methodName = element.methodName
//            "$className.$methodName: "
//        } else {
//            ""
//        }
//
//        super.log(priority, newTag, prefix + message, t)
//    }
//}