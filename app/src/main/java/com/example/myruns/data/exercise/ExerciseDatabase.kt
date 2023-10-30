package com.example.myruns.data.exercise

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.myruns.data.CalendarTypeConverter
import org.w3c.dom.Comment

@Database(entities = [ExerciseEntry::class], version = 1)
@TypeConverters(CalendarTypeConverter::class)
abstract class ExerciseDatabase : RoomDatabase() {
    abstract val exerciseDatabaseDao: ExerciseDatabaseDao

    companion object {
        @Volatile
        private var DATABASE_INSTANCE: ExerciseDatabase? = null

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