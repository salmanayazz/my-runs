package com.example.salman_ayaz_myruns.data.exercise

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.salman_ayaz_myruns.data.CalendarTypeConverter

/**
 * database for the ExerciseEntry class
 */
@Database(entities = [ExerciseEntry::class], version = 1)
@TypeConverters(CalendarTypeConverter::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract val exerciseDatabaseDao: ExerciseDatabaseDao

    companion object {
        @Volatile
        private var DATABASE_INSTANCE: ExerciseDatabase? = null

        /**
         * returns the singleton instance of the database
         * @param context 
         * the context of the application
         * @return ExerciseDatabase
         * the singleton instance of the database
         */
        fun getInstance(context: Context) : ExerciseDatabase{
            synchronized(this){
                var databaseInstance = DATABASE_INSTANCE
                if (databaseInstance == null) {
                    databaseInstance = Room.databaseBuilder(context.applicationContext, ExerciseDatabase::class.java, "exercise_table").build()
                    DATABASE_INSTANCE = databaseInstance
                }
                return databaseInstance
            }
        }
    }
}