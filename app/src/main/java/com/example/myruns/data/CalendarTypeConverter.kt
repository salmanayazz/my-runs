package com.example.myruns.data

import androidx.room.TypeConverter
import java.util.Calendar

class CalendarTypeConverter {
    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

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