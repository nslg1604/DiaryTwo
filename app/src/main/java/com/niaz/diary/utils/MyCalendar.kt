package com.niaz.diary.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar


class MyCalendar () {
    var daysOfWeek = arrayOf("понедельник", "вторник", "среда", "четверг",
        "пятница", "суббота", "воскресенье")
    var daysOfWeekShort = arrayOf("пн", "вт", "ср", "чт",
        "пт", "сб", "вс")
    var months = arrayOf("января","февраля","марта","апреля","мая","июня",
        "июля","августа","сентября","октября","ноября","декабря")


    fun calendarToDD_MM_YYYY(calendar: Calendar?): String {
        if (calendar == null){
            return ""
        }
        val df = SimpleDateFormat("dd.MM.yyyy")
        val formattedDate = df.format(calendar.time)
        return formattedDate
    }

    fun calendarToYYYY_MM_DD(calendar: Calendar?): String {
        if (calendar == null){
            return ""
        }
        val df = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = df.format(calendar.time)
        return formattedDate
    }

    /**
     * Convert date string to calendar
     */
    fun dateYYYYMMDDtoCalendar(dateStr: String?): Calendar? {
        var calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        try {
            calendar.time = sdf.parse(dateStr)
        } catch (e: ParseException) {
            MyLogger.e("MyCalendar - dateYYYYMMDDtoCalendar error=$e")
            calendar = null
        }
        return calendar
    }

    /**
     * Convert date string to calendar
     * 2021-05-26T08:31:19
     */
    fun dateYYYYMMDDHHMMSStoCalendar(dateStr: String?): Calendar? {
        var calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            calendar.time = sdf.parse(dateStr)
        } catch (e: ParseException) {
            MyLogger.e("MyCalendar - dateYYYYMMDDHHMMSStoCalendar error=$e")
            calendar = null
        }
        return calendar
    }

    fun calendarToHH_MM(calendar: Calendar?): String {
        if (calendar == null){
            return ""
        }
        val df = SimpleDateFormat("HH:mm")
        val formattedDate = df.format(calendar.time)
        return formattedDate
    }

    fun calendarToDDMMMYYYY(calendar: Calendar?): String {
        if (calendar == null){
            return ""
        }
//        val df = SimpleDateFormat("dd && yyyy г.")
        val df = SimpleDateFormat("dd && yyyy")
        var formattedDate = df.format(calendar.time)
        var month:Int = calendar.get(Calendar.MONTH)
//        MyLogger.d("MyCalendar - calendarToDDMMMYYYY month=" + month)
        if (month < 12) {
            formattedDate = formattedDate.replace("&&", months[month])
        }
        return formattedDate
    }



    /**
     * Get date as string
     */
    fun todayDD_MM_YYYY(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("dd.MM.yyyy")
        val formattedDate = df.format(c.time)
        return formattedDate
    }

    fun getDayOfWeek(calendar: Calendar):String{
//        return DateTimeFormatter.ofPattern("EEEE").format(LocalDate.now()))
        var day = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (day < 0){
            day += 7
        }
        return daysOfWeek[day]
    }

    fun getDayOfWeekShort(calendar: Calendar):String{
//        return DateTimeFormatter.ofPattern("EEEE").format(LocalDate.now()))
        var day = calendar.get(Calendar.DAY_OF_WEEK) - 2
        if (day < 0){
            day += 7
        }
        return daysOfWeekShort[day]
    }

    fun dateYYYYMMDDtoDDMMMYYYY(str:String?):String{
        if (str == null){
            return ""
        }
        val calendar = dateYYYYMMDDtoCalendar(str);
        if (calendar == null) {
            return ""
        }

        return calendarToDDMMMYYYY(calendar)

    }

//    /* По указанной дате получаем день недели*/
//    fun getDayOfWeek(date: LocalDate): String {
//        val dayOfWeek = date.dayOfWeek
//        println("dayOfWeek.toString()$dayOfWeek")
//        val localeRu = Locale("ru", "RU")
//
//        return dayOfWeek.getDisplayName(TextStyle.FULL, localeRu)
//    }
}