package com.example.myruns.data.exercise;


import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters
import com.example.myruns.data.CalendarTypeConverter
import com.example.myruns.data.LatLngTypeConverter
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.util.Calendar

/**
 * represents an exercise entry
 * @param id
 * the id of the exercise
 * @param inputType
 * the input type of the exercise
 * @param activityType
 * the activity type of the exercise
 * @param dateTime
 * the date and time of the exercise
 * @param duration
 * the duration in mins
 * @param distance
 * the distance in kilometers 
 * (the db stores the distance in kilometers and converts if needed)
 * @param avgPace
 * speed in km/h
 * @param calories
 * the amount of calories burned
 * @param climb
 * height climbed in km (will be negative if downhill)
 * @param heartRate
 * heart rate in bpm
 * @param comment
 * any comments the user wrote about the exercise
 */
@Entity(tableName = "exercise_table")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val inputType: String?,
    val activityType: Int,
    val dateTime: Calendar?,
    val duration: Double?,
    val distance: Double?,
    var avgPace: Double?,
    val calories: Double?,
    val climb: Double?,
    val heartRate: Double?,
    val comment: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val locationList: ArrayList<LatLng>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        Calendar.getInstance().apply {
            timeInMillis = parcel.readLong()
        },
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.createTypedArrayList(LatLng.CREATOR)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(inputType)
        parcel.writeInt(activityType)
        parcel.writeLong(dateTime?.timeInMillis ?: 0L)
        parcel.writeValue(duration)
        parcel.writeValue(distance)
        parcel.writeValue(avgPace)
        parcel.writeValue(calories)
        parcel.writeValue(climb)
        parcel.writeValue(heartRate)
        parcel.writeString(comment)
        parcel.writeTypedList(locationList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExerciseEntry> {
        override fun createFromParcel(parcel: Parcel): ExerciseEntry {
            return ExerciseEntry(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseEntry?> {
            return arrayOfNulls(size)
        }
    }
}

