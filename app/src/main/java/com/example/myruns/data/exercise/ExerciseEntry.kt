package com.example.myruns.data.exercise;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Calendar

@Entity(tableName = "exercise_table")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val inputType: String,
    val activityType: Int,
    val dateTime: Calendar?,
    val duration: Int?,
    val distance: Int?,
    val avgPace: Int?,
    val calories: Double?,
    val climb: Double?,
    val heartRate: Double?,
    val comment: String?,
    //val locationList: ArrayList<LatLng>
)

