package com.example.myruns.ui.manualinput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar

class ManualInputViewModel(private val repository: ExerciseRepository): ViewModel() {
    enum class DialogOptions {
        DURATION,
        DISTANCE,
        CALORIES,
        HEART_RATE,
        COMMENTS
    }
    var dialogOpen = MutableLiveData<DialogOptions>(null)

    var dateTime = MutableLiveData(Calendar.getInstance())
    var pickedDate = false
    var pickedTime = false

    var duration = MutableLiveData<Int>()
    var distance = MutableLiveData<Int>()
    var calories = MutableLiveData<Double>()
    var heartRate = MutableLiveData<Double>()
    var comments = MutableLiveData<String>()

    fun insert() {
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
        ))
    }

    fun getAll() {
        println("all comments:")
        CoroutineScope(IO).launch{
            repository.allComments.collect() {
                println(it)
            }
        }


    }
}