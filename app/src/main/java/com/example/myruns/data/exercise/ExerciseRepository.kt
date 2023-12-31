package com.example.myruns.data.exercise

import com.example.myruns.ui.SettingsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * repository for the ExerciseEntry database
 * @param exerciseDatabaseDao
 * the dao for the ExerciseEntry database
 */
class ExerciseRepository(private val exerciseDatabaseDao: ExerciseDatabaseDao) {
    private val kmToMilesConversion = SettingsFragment.KM_TO_MILES

    /**
     * returns a Flow of all ExerciseEntries in the database
     * @param unitType 
     * the unit type to display the distance in
     * which should be either SettingsFragment.UNIT_METRIC or SettingsFragment.UNIT_IMPERIAL
     */
    fun allExerciseEntries(unitType: String): Flow<List<ExerciseEntry>> {
        val exerciseEntries = exerciseDatabaseDao.getAllExercises()
        return if (unitType == SettingsFragment.UNIT_METRIC) {
            exerciseEntries
        } else {
            exerciseEntries.map { entries ->
                entries.map { entry ->
                    entry.copy(distance = entry.distance?.times(kmToMilesConversion))
                }
            }
        }
    }

    /**
     * inserts an exercise entry into the database
     * @param exerciseEntry 
     * the exercise entry to insert
     * @param unitType
     * the unit type to display the distance in
     * which should be either SettingsFragment.UNIT_METRIC or SettingsFragment.UNIT_IMPERIAL
     */
    fun insert(exerciseEntry: ExerciseEntry, unitType: String) {
        var exerciseEntryCopy = exerciseEntry
        if (unitType == SettingsFragment.UNIT_IMPERIAL) {

            exerciseEntryCopy = exerciseEntry.copy(distance = exerciseEntry.distance?.div(kmToMilesConversion))
            println("the value was miles ${exerciseEntryCopy.distance}")
        }
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.insertExercise(exerciseEntryCopy)
        }
    }

    /**
     * deletes an exercise entry from the database
     * @param id 
     * the id of the exercise entry to delete
     */
    fun delete(id: Long) {
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.deleteExercise(id)
        }
    }

    /**
     * deletes all exercise entries from the database
     */
    fun deleteAll() {
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.deleteAll()
        }
    }
}