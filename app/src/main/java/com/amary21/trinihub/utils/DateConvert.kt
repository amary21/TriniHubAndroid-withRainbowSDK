package com.amary21.trinihub.utils

import java.text.SimpleDateFormat
import java.util.*

object DateConvert {

    fun convertDat2e(dateEvent: String): String {
        var date = dateEvent
        try {
            var simpleDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
            val convertDate = simpleDateFormat.parse(date)
            simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            date = simpleDateFormat.format(convertDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    fun convertDateMeet(dateEvent: String): String {
        var date = dateEvent
        try {
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val convertDate = simpleDateFormat.parse(date)
            simpleDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
            date = simpleDateFormat.format(convertDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return date
    }

    fun prettyFormat(dateMessage : Date): String {

        val calendarToday = Calendar.getInstance()
        val calendarMessage = Calendar.getInstance()
        val format: SimpleDateFormat

        calendarToday.time = Date()
        calendarMessage.time = dateMessage

        format = when {
            calendarMessage.get(Calendar.YEAR) != calendarToday.get(Calendar.YEAR) -> SimpleDateFormat("dd mmm yyyy hh:mm", Locale.getDefault())
            calendarMessage.get(Calendar.DAY_OF_YEAR) != calendarToday.get(Calendar.DAY_OF_YEAR) -> SimpleDateFormat("EEE hh:mm", Locale.getDefault())
            else -> SimpleDateFormat("hh:mm", Locale.getDefault())
        }

        return format.format(dateMessage)
    }

    fun prettyFormatTime(dateMessage : Date): String {

        val calendarToday = Calendar.getInstance()
        val calendarMessage = Calendar.getInstance()

        calendarToday.time = Date()
        calendarMessage.time = dateMessage

        val format = SimpleDateFormat("hh:mm", Locale.getDefault())

        return format.format(dateMessage)
    }

    fun dateNow(dateMessage: String) : Boolean{
        val now: Boolean?
        val calendar = Calendar.getInstance()
        val dateNow = calendar.time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val mDateNow = dateFormat.format(dateNow)
        val mDateMessage = convertDat2e(dateMessage)

        now = mDateMessage == mDateNow

        return now
    }

}