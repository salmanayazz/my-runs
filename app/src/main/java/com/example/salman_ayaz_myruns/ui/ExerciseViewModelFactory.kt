package com.example.salman_ayaz_myruns.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.salman_ayaz_myruns.data.exercise.ExerciseRepository
import com.example.salman_ayaz_myruns.ui.history.HistoryViewModel
import com.example.salman_ayaz_myruns.ui.manualinput.ManualInputViewModel

/**
 * factory for creating ViewModels for use with the ExerciseRepository
 */
class ExerciseViewModelFactory (private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(ManualInputViewModel::class.java)) {
            return ManualInputViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}