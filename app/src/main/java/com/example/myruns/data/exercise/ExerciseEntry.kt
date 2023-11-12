package com.example.myruns.data.exercise;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
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
 * TODO
 * @param calories
 * the amount of calories burned
 * @param climb
 * TODO
 * @param heartRate
 * heart rate in bpm
 * @param comment
 * any comments the user wrote about the exercise
 */
@Entity(tableName = "exercise_table")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val inputType: String,
    val activityType: Int,
    val dateTime: Calendar?,
    val duration: Double?,
    val distance: Double?,
    val avgPace: Double?,
    val calories: Double?,
    val climb: Double?,
    val heartRate: Double?,
    val comment: String?,
    //val locationList: ArrayList<LatLng>
): Serializable

