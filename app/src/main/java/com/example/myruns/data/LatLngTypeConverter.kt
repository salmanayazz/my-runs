package com.example.myruns.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LatLngTypeConverter {
    
    /**
     * converts an ArrayList of LatLngs to a string
     * @param value
     * the ArrayList of LatLngs to convert
     */
    @TypeConverter
    fun fromLatLngList(value: ArrayList<LatLng>?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    /**
     * converts a string to an ArrayList of LatLngs
     * @param value
     * the string to convert
     */
    @TypeConverter
    fun toLatLngList(value: String?): ArrayList<LatLng>? {
        val listType = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
