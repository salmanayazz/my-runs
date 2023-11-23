package com.example.myruns.ui.mapdisplay

import androidx.lifecycle.ViewModel
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.SettingsFragment

class MapDisplayViewModel(private val repository: ExerciseRepository): ViewModel() {
    fun insert(exerciseEntry: ExerciseEntry) {
        // the unit type is always metric
        repository.insert(exerciseEntry, SettingsFragment.UNIT_METRIC)
    }

    fun delete(id: Long) {
        repository.delete(id)
    }

}