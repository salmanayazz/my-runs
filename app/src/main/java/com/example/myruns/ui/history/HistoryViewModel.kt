package com.example.myruns.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import kotlinx.coroutines.flow.Flow

/**
 * view model for the HistoryFragment
 * @param repository
 * the repository for the ExerciseEntry database
 */
class HistoryViewModel(private val repository: ExerciseRepository) : ViewModel() {
    val unitPreference = MutableLiveData<String>()
    private val exerciseEntriesSource = MutableLiveData<Flow<List<ExerciseEntry>>>()

    val exerciseEntries = exerciseEntriesSource.switchMap { flow ->
        flow.asLiveData()
    }

    init {
        // set up an observer on the unitPreference LiveData
        unitPreference.observeForever { unitType ->
            val newExerciseEntries = repository.allExerciseEntries(unitType)
            exerciseEntriesSource.value = newExerciseEntries
        }
    }

    fun delete(id: Long) {
        repository.delete(id)
    }
}