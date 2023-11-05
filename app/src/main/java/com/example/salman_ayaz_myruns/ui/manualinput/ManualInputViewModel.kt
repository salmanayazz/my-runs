package com.example.salman_ayaz_myruns.ui.manualinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.salman_ayaz_myruns.data.exercise.ExerciseEntry
import com.example.salman_ayaz_myruns.data.exercise.ExerciseRepository
import com.example.salman_ayaz_myruns.ui.StartFragment
import java.util.Calendar

/**
 * view model for the ManualInputFragment
 * @param repository
 * the repository for the ExerciseEntry database
 */
class ManualInputViewModel(private val repository: ExerciseRepository): ViewModel() {
    /**
     * the options for the dialog that pops up when the user 
     * clicks on a field in the ManualInputFragment
     */
    enum class DialogOptions {
        DURATION,
        DISTANCE,
        CALORIES,
        HEART_RATE,
        COMMENTS
    }
    /** to reopen dialog on orientation change */
    var dialogOpen = MutableLiveData<DialogOptions>(null)

    var dateTime = MutableLiveData(Calendar.getInstance())

    var duration = MutableLiveData<Double>()
    var distance = MutableLiveData<Double>()
    var calories = MutableLiveData<Double>()
    var heartRate = MutableLiveData<Double>()
    var comments = MutableLiveData<String>()

    val unitPreference = MutableLiveData<String>()

    /** activity type index for StartFragment.activityTypeList */
    var activityType: Int = 0

    /**
     * inserts an exercise entry into the database
     */
    fun insert() {
        unitPreference.value?.let {
            repository.insert(ExerciseEntry(
                0,
                StartFragment.INPUT_TYPE_MANUAL,
                activityType,
                dateTime.value,
                duration.value,
                distance.value,
                0.0,
                calories.value,
                0.0,
                heartRate.value,
                comments.value
            ), it
            )
        }
    }
}