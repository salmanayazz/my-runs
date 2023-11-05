package com.example.salman_ayaz_myruns.data

import androidx.room.TypeConverter
import java.util.Calendar

class CalendarTypeConverter {

    /**
     * converts a calendar to a time in milliseconds
     * @param calendar
     * the calendar to be converted
     */
    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    /**
     * converts a time in milliseconds to a calendar
     * @param timeInMillis
     * the time in milliseconds
     */
    @TypeConverter
    fun toCalendar(timeInMillis: Long?): Calendar? {
        if (timeInMillis != null) {
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            return calendar
        }
        return null
    }

}