package com.example.myruns.ui.mapdisplay

import androidx.lifecycle.ViewModel
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.SettingsFragment
import kotlinx.coroutines.flow.Flow

class MapDisplayViewModel(private val repository: ExerciseRepository): ViewModel() {
    fun insert(exerciseEntry: ExerciseEntry) {
        repository.insert(exerciseEntry, SettingsFragment.UNIT_METRIC) // TODO: change depending on user's preference
    }

    fun delete(id: Long) {
        repository.delete(id)
    }

}