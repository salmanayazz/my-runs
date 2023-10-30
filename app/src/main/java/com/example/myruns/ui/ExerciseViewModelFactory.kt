package com.example.myruns.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.manualinput.ManualInputViewModel

class ExerciseViewModelFactory (private val repository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create (modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(ManualInputViewModel::class.java)) {
            return ManualInputViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}