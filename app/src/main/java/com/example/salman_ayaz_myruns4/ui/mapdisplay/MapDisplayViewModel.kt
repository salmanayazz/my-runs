package com.example.salman_ayaz_myruns4.ui.mapdisplay

import androidx.lifecycle.ViewModel
import com.example.salman_ayaz_myruns4.data.exercise.ExerciseEntry
import com.example.salman_ayaz_myruns4.data.exercise.ExerciseRepository
import com.example.salman_ayaz_myruns4.ui.SettingsFragment
import kotlinx.coroutines.flow.Flow

class MapDisplayViewModel(private val repository: ExerciseRepository): ViewModel() {
    fun insert(exerciseEntry: ExerciseEntry) {
        // the unit type is always metric
        repository.insert(exerciseEntry, SettingsFragment.UNIT_METRIC)
    }

    fun delete(id: Long) {
        repository.delete(id)
    }

}