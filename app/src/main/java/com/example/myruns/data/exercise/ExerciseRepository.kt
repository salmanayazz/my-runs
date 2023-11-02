package com.example.myruns.data.exercise

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseRepository(private val commentDatabaseDao: ExerciseDatabaseDao) {

    val allComments: Flow<List<ExerciseEntry>> = commentDatabaseDao.getAllExercises()

    fun insert(exerciseEntry: ExerciseEntry){
        CoroutineScope(IO).launch{
            commentDatabaseDao.insertExercise(exerciseEntry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            commentDatabaseDao.deleteExercise(id)
        }
    }

    fun deleteAll() {
        CoroutineScope(IO).launch {
            commentDatabaseDao.deleteAll()
        }
    }
}