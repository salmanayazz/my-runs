package com.example.myruns.data.exercise;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDatabaseDao {

    /**
     * inserts the given exercise into the database
     * @param exerciseEntry
     * the exercise to be inserted
     */
    @Insert
    suspend fun insertExercise(exerciseEntry: ExerciseEntry)

    /**
     * returns all the exercises in the database
     * @return Flow<List<ExerciseEntry>>
     * a list of all the exercises
     */
    @Query("SELECT * FROM exercise_table ORDER BY dateTime DESC")
    fun getAllExercises(): Flow<List<ExerciseEntry>>

    /**
     * returns the exercise with the given id
     * @param key
     * the id of the exercise
     */
    @Query("DELETE FROM exercise_table WHERE id = :key")
    suspend fun deleteExercise(key: Long)

    /**
     * deletes all the exercises in the database
     */
    @Query("DELETE FROM exercise_table")
    suspend fun deleteAll()
}
