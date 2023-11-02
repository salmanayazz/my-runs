package com.example.myruns.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.myruns.data.exercise.ExerciseRepository

class HistoryViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val exerciseEntries = repository.allComments.asLiveData()
}