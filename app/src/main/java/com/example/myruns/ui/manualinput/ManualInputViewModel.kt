package com.example.myruns.ui.manualinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
    var dialogOpen = MutableLiveData<DialogOptions>(null)

    var dateTime = MutableLiveData(Calendar.getInstance())

    var duration = MutableLiveData<Int>()
    var distance = MutableLiveData<Int>()
    var calories = MutableLiveData<Double>()
    var heartRate = MutableLiveData<Double>()
    var comments = MutableLiveData<String>()

    val unitPreference = MutableLiveData<String>()

    /**
     * inserts an exercise entry into the database
     */
    fun insert() {
        unitPreference.value?.let {
            repository.insert(ExerciseEntry(
                0,
                "Manual Input",
                0, // TODO: CHANGE THIS VALUE
                dateTime.value,
                duration.value,
                distance.value,
                0,
                calories.value,
                0.0,
                heartRate.value,
                comments.value
            ), it
            )
        }
    }
}