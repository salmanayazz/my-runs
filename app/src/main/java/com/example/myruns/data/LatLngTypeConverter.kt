package com.example.myruns.data

import android.os.Build
import android.os.Parcel
import androidx.room.TypeConverter
import com.example.myruns.TrackingService
import com.example.myruns.data.exercise.ExerciseEntry
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class LatLngTypeConverter {
    @TypeConverter
    fun fromLatLngList(latLngList: ArrayList<LatLng>?): ByteArray? {
        if (latLngList == null) return null

        val parcel = Parcel.obtain()
        latLngList.forEach {
            parcel.writeParcelable(it, 0)
        }
        val byteArray = parcel.marshall()
        parcel.recycle()

        return byteArray
    }

    @TypeConverter
    fun toLatLngList(byteArray: ByteArray?): ArrayList<LatLng>? {
        if (byteArray == null) return null

        val parcel = Parcel.obtain()
        parcel.unmarshall(byteArray, 0, byteArray.size)
        parcel.setDataPosition(0)

        val latLngList = ArrayList<LatLng>()
        while (parcel.dataAvail() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                latLngList.add(parcel.readParcelable(LatLng::class.java.classLoader, LatLng::class.java)!!)
            } else {
                latLngList.add(parcel.readParcelable(LatLng::class.java.classLoader)!!)
            }
        }

        parcel.recycle()
        return latLngList
    }
}
