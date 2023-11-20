package com.example.salman_ayaz_myruns4.data.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.salman_ayaz_myruns4.ui.history.HistoryViewModel
import com.example.salman_ayaz_myruns4.ui.manualinput.ManualInputViewModel
import com.example.salman_ayaz_myruns4.ui.mapdisplay.MapDisplayViewModel

/**
 * factory for creating ViewModels for use with the ExerciseRepository
 */
class ExerciseViewModelFactory (private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(ManualInputViewModel::class.java)) {
            return ManualInputViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(MapDisplayViewModel::class.java)) {
            return MapDisplayViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}