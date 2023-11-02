package com.example.myruns.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import com.example.myruns.R
import com.example.myruns.data.exercise.ExerciseDatabase
import com.example.myruns.data.exercise.ExerciseEntry
import com.example.myruns.data.exercise.ExerciseRepository
import com.example.myruns.ui.ExerciseViewModelFactory
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.text.SimpleDateFormat

class HistoryEntryActivity : AppCompatActivity() {
    private var exerciseEntry: ExerciseEntry? = null
    companion object {
        const val EXERCISE_ENTRY = "exercise_entry"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_entry)

        // get the ExerciseEntry as a byte array
        val exerciseByteArr = intent.getByteArrayExtra(EXERCISE_ENTRY)

        // setup mvvm objects
        var database = ExerciseDatabase.getInstance(this)
        var databaseDao = database.exerciseDatabaseDao
        var repository = ExerciseRepository(databaseDao)
        var viewModelFactory = ExerciseViewModelFactory(repository)
        var historyViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[HistoryViewModel::class.java]

        try {
            // parse into ExerciseEntry if not null
            exerciseEntry = ObjectInputStream(
                ByteArrayInputStream(exerciseByteArr)
            ).readObject() as ExerciseEntry

            populateText()
        } catch (e: Exception) {
            println(e.printStackTrace())
        }

        // delete entry onClick
        findViewById<ImageView>(R.id.delete_entry).setOnClickListener() {
            exerciseEntry?.id?.let {
                    it1 -> historyViewModel.delete(it1)
            }
            this.finish()
        }

    }

    private fun populateText() {
        if (exerciseEntry != null) {
//            findViewById<EditText>(R.id.date)
//                .setText(SimpleDateFormat("yyyy-MM-dd")
//                    .format(exerciseEntry!!.dateTime))
//
//            findViewById<EditText>(R.id.time)
//                .setText(SimpleDateFormat("HH:mm")
//                    .format(exerciseEntry!!.dateTime))

            findViewById<EditText>(R.id.duration)
                .setText(exerciseEntry!!.duration?.toString() ?: "")

            findViewById<EditText>(R.id.distance)
                .setText(exerciseEntry!!.distance?.toString() ?: "")

            findViewById<EditText>(R.id.calories)
                .setText(exerciseEntry!!.calories?.toString() ?: "")

            findViewById<EditText>(R.id.heart_rate)
                .setText(exerciseEntry!!.heartRate?.toString() ?: "")

            findViewById<EditText>(R.id.comments)
                .setText(exerciseEntry!!.comment ?: "")

        }

    }
}